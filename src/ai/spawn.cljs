(ns ai.spawn
  (:require [screeps.game :as game]
            [screeps.creep :as creep]
            [screeps.spawn :as spawn]
            [screeps.room :as room]
            [screeps.memory :as memory])
  (:use [ai.room :only [init-sources]]))

(defn ^:export create-template
  [sp role]
  (let [template (spawn/templates role)
        energy (room/available-energy (spawn/room sp))
        new-creep (.createCreep sp (clj->js (spawn/cap-template template energy)) nil nil)]
    (when (string? new-creep)
      (memory/store-in ["creeps" new-creep] {"arch" role "size" (spawn/body-cost template)}))))

(defn create-miner
  [sp]
  (create-template sp "miner"))

(defn create-courier
  [sp]
  (create-template sp "courier"))


(defn filter-by-role
  [role creeps]
  (filter #(= role ((creep/memory %) "arch")) creeps))

(defn ^:export free-source-slots
  [room]
  (let [m (room/memory room)
        sources (or (get m "sources")
                    (init-sources room))
        free (reduce + (map #(get-in sources [% "free"]) (keys sources)))]
    free))

(defn can-spawn-best?
  [room template]
  (<= (spawn/body-cost (spawn/cap-template (spawn/templates template) (room/energy-capacity room)))
      (room/available-energy room)))

(defn run-spawn
  [sp]
  (let [room (spawn/room sp)
        energy (room/available-energy room)
        capacity (room/energy-capacity room)]
    (when (and (not (spawn/spawning? sp))
               (<= 300 energy))
      (let [free-slots (free-source-slots room)
            source-count (count ((room/memory room) "sources"))
            creeps (game/creeps)
            miners (filter-by-role "miner" creeps)
            couriers (filter-by-role "courier" creeps)]
        (cond
          (or (nil? couriers) (= 0 (count couriers)))
          (do (.log js/console "making a bootstrapper")
              (create-courier sp))

          (and (< (count miners) source-count)
               (can-spawn-best? room "miner"))
          (do (.log js/console "making miners~")
              (create-miner sp)
              (when-not (= free-slots (- source-count (count miners)))
                (.log js/console "miner down! miner down!!")
                (init-sources room)))

          (and (< (count couriers) (* 3 (count ((room/memory room) "sources"))))
               (can-spawn-best? room "courier"))
          (do (.log js/console "couriers it is")
              (create-courier sp)))))))
