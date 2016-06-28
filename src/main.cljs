(ns main
  (:require [screeps.game :as game]
            [screeps.creep :as creep]
            [screeps.spawn :as spawn]
            [screeps.room :as room]
            [screeps.memory :as memory]
            [screeps.structure :as structure])
  (:use [ai.creep :only (run-creep renew-creep)]
        [ai.spawn :only (run-spawn)]
        [ai.tower :only (run-tower)]))

(defn main-loop
  []
  (memory/load-memory)
  (let [creeps (game/creeps)]
    (doseq [creep creeps]
      (run-creep creep)))
  (let [spawns (game/spawns)]
    (doseq [spawn spawns]
      (run-spawn spawn)))
  (let [rooms (game/rooms)
        towers (filter identity (flatten (map (fn [room] (room/find room js/FIND_MY_STRUCTURES #(= (structure/type %) js/STRUCTURE_TOWER))) rooms)))]
    (doseq [tower towers]
      (run-tower tower)))
  (memory/write-memory!))

(set! js/module.exports.loop main-loop)
