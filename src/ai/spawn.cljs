(ns ai.spawn
  (:require [screeps.game :as game]
            [screeps.creep :as creep]
            [screeps.spawn :as spawn]
            [screeps.room :as room]))

(defn filter-by-role
  [role creeps]
  (filter #(= role (:role (creep/memory %))) creeps))

(defn run-spawn
  [sp]
  #_(when (= (spawn/energy sp) (spawn/energy-capacity sp))
    (let [room (spawn/room sp)
          sources (room/find room js/FIND_SOURCES_ACTIVE)
          creeps (game/creeps)
          miners (filter-by-role "miner" creeps)
          couriers (filter-by-role "courier" creeps)]
      (when (= (room/name room) "sim")
        (cond
          (< (count miners) (count sources))
          (spawn/create-miner sp)

          (< (count couriers) (count sources))
          (spawn/create-courier sp))))))
