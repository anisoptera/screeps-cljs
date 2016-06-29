(ns main
  (:require [screeps.game :as game]
            [screeps.creep :as creep]
            [screeps.spawn :as spawn]
            [screeps.room :as room]
            [screeps.memory :as memory]
            [screeps.structure :as structure]))

(defn main-loop
  []
  (memory/load-memory)
  ;; your code goes here lol
  (memory/write-memory!))

(set! js/module.exports.loop main-loop)
