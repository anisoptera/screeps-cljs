(ns ai.test
  (:require [screeps.game :as game]
            [screeps.creep :as creep]
            [screeps.room :as room]
            [screeps.spawn :as spawn]
            [screeps.position :as pos]
            [screeps.structure :as structure]
            [screeps.memory :as m]))

(defn test-game
  []
  (.log js/console "time: " (game/time))
  (.log js/console "creeps: " (mapv creep/name (game/creeps)))
  (let [id (-> (game/creeps) first (.-id))]
    (.log js/console "object " id " - " (game/object id))
    )

  (.log js/console "used-cpu: " (game/used-cpu)))

(defn test-creep
  [c]
  (when c
    (.log js/console "creep name" (creep/name c))
    (let [sp1 (game/spawns "Spawn1")
          sp2 (game/spawns "Spawn2")]
      (if (pos/next-to? c sp1)
        (creep/move-to c sp2))
      (if (pos/next-to? c sp2)
        (creep/move-to c sp1)))
    (.log js/console "position p: " (pos/position c))))

(defn test-position
  []
  (.log js/console "type RoomPosition: " (type js/RoomPosition))
  (let [p (pos/create 5 5 "sim")]
    (.log js/console "position " (js->clj p) " type " (type p))
    )
  )

(defn test-memory
  []
  (let [t {:a 1 :b "two"}]
    (m/store! :test t))

  (m/update! :inc (fnil inc 0))
  (.log js/console "memory: " (@m/*memory* :test))
  (.log js/console "memory: " (@m/*memory* :inc))
  )

(defn test-room
  []
  (let [sim-room (game/rooms "sim")
        spawns (room/find sim-room js/FIND_MY_SPAWNS)]
    (.log js/console "spawns: " spawns)
    (.log js/console "creeps: " (room/find sim-room js/FIND_CREEPS #(= "Abigail" (creep/name %))))
    (.log js/console "path: " (room/find-path sim-room (pos/create 5 5 "sim") (pos/create 10 10 "sim")))
    (.log js/console "look [5 5]: " (room/look sim-room 5 5))
    (.log js/console "look for 'terrain' [5 5]: " (room/look-for sim-room 5 5 "terrain"))
    (.log js/console "look for 'road' [5 5]: " (room/look-for sim-room 5 5 "terrain"))
    (room/create-construction-site sim-room 5 5 js/STRUCTURE_ROAD)
    (.log js/console "road const: " js/STRUCTURE_ROAD)

    )
  )

#_(test-game)

#_(test-creep (-> (game/creeps) first))

#_(test-position)
#_(test-memory)

#_(test-room)
