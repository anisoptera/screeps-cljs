(ns ai.tower
  (:require [screeps.game :as game]
            [screeps.creep :as creep]
            [screeps.room :as room]
            [screeps.spawn :as spawn]
            [screeps.position :as pos]
            [screeps.structure :as structure]
            [screeps.memory :as m]))

(def desired-wall-strength 1000)

(defn select-repair-candidate
  [room]
  (let [walls (room/find room js/FIND_STRUCTURES
                         #(and (= (structure/type %) js/STRUCTURE_WALL)
                               (< (structure/hits %) desired-wall-strength)))
        roads (room/find room js/FIND_STRUCTURES
                         #(and (= (structure/type %) js/STRUCTURE_ROAD)
                               (< 1000 (- (structure/max-hits %) (structure/hits %)))))

        containers (room/find room js/FIND_STRUCTURES
                              #(and (= (structure/type %) js/STRUCTURE_CONTAINER)
                                    (< 50000 (- (structure/max-hits %) (structure/hits %)))))

        structs (room/find room js/FIND_MY_STRUCTURES #(< (structure/hits %) (structure/max-hits %)))]
    (cond
      (not= 0 (count walls))
      (first (sort-by structure/hits walls))

      (not= 0 (count roads))
      (first (sort-by structure/hits roads))

      (not= 0 (count containers))
      (first (sort-by structure/hits containers))

      (not= 0 (count structs))
      (first (sort-by structure/hits structs))

      :else nil)))

(defn run-tower
  [tower]
  (let [room (structure/room tower)
        energy (structure/energy tower)]
    (when (> energy 200)
      (when-let [repair-target (select-repair-candidate room)]
        (.repair tower repair-target)))))
