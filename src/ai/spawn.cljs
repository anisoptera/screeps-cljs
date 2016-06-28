(ns ai.spawn
  (:require [screeps.game :as game]
            [screeps.creep :as creep]
            [screeps.spawn :as spawn]
            [screeps.room :as room])
  (:use [ai.creep :only [init-sources]]
        [screeps.utils :only [jsx->clj]]))

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

(defn run-spawn
  [sp]
  (let [room (spawn/room sp)]
    (when (and (not (spawn/spawning? sp))
               (= (room/available-energy room) (room/energy-capacity room)))
      (let [free-slots (free-source-slots room)
            creeps (game/creeps)
            miners (filter-by-role "miner" creeps)
            couriers (filter-by-role "courier" creeps)]
        (cond
          (or (nil? couriers) (= 0 (count couriers)))
          (do (.log js/console "making a bootstrapper") (spawn/create-courier sp))

          (< 0 free-slots)
          (do (.log js/console "making miners~") (spawn/create-miner sp))

          (< (count couriers) (count miners))
          (do (.log js/console "couriers it is") (spawn/create-courier sp)))))))
