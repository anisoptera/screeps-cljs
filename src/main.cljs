(ns main
  (:require [screeps.game :as game]
            [screeps.creep :as creep]
            [screeps.spawn :as spawn])
  (:use [ai.creep :only (run-creep renew-creep)]
        [ai.spawn :only (run-spawn)]))

(defn main-loop
  []
  (let [creeps (game/creeps)]
    (doseq [creep creeps]
      (let [m (creep/memory creep)]
        (if (:dump m)
          (run-creep creep)
          (if (or (< (creep/ttl creep) 200) (:renewing m))
            (if (> (creep/ttl creep) 1000)
              (do
                (creep/memory! creep (assoc m :renewing false))
                (run-creep creep))
              (do
                (creep/memory! creep (assoc m :renewing true))
                (renew-creep creep)))
            (run-creep creep))))))
  (let [spawns (game/spawns)]
    (doseq [spawn spawns]
      (run-spawn spawn))))

(set! js/module.exports.loop main-loop)
