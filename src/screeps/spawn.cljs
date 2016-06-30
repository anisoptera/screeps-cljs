(ns screeps.spawn
  (:refer-clojure :exclude [name])
  (:require [screeps.creep :as creep]
            [screeps.room :as room])
  (:use [screeps.memory :only [*memory*]]))

(defn name
  [s]
  (.-name s))

(defn room
  [s]
  (.-room s))

(defn energy-capacity
  [s]
  (.-energyCapacity s))

(defn spawning?
  [s]
  (.-spawning s))

(defn create-creep
  [sp body]
  (.createCreep sp (clj->js body) nil nil))

(defn renew-creep
  [sp creep]
  (.renewCreep sp creep))

(defn energy
  [sp]
  (.-energy sp))

;; these are here and not in ai.spawn because some stuff in ai.creep needs them for now.
(def costs
  {js/WORK 100
   js/MOVE 50
   js/CARRY 50
   js/ATTACK 80
   js/RANGED_ATTACK 150
   js/HEAL 250
   js/CLAIM 600
   js/TOUGH 10})

(defn body-cost
  [body]
  (reduce + (map costs body)))

(defn cap-template
  [[template & rest] max-cost]
  (if (or (<= (body-cost template) max-cost)
          (nil? template))
    template
    (recur rest max-cost)))

(def templates
  {"miner"
   [[js/MOVE js/MOVE js/CARRY js/WORK js/WORK js/WORK js/WORK js/WORK]
    [js/MOVE js/MOVE js/CARRY js/WORK js/WORK js/WORK js/WORK] ; 550
    [js/MOVE js/CARRY js/WORK js/WORK] ; 300
    ]
   
   "rescue"
   [[js/MOVE js/WORK js/CARRY js/CARRY js/MOVE]]

   "courier"
   [[js/MOVE js/CARRY js/CARRY js/WORK js/MOVE js/CARRY js/CARRY js/WORK js/MOVE js/CARRY js/CARRY js/CARRY js/CARRY js/CARRY js/CARRY js/MOVE js/MOVE js/MOVE js/WORK js/WORK js/MOVE]  ; 1300 - carries 500, moves 1/tick on roads, has 4 WORKs
    [js/MOVE js/CARRY js/CARRY js/WORK js/MOVE js/CARRY js/CARRY js/WORK js/MOVE js/CARRY js/CARRY js/CARRY js/CARRY js/CARRY] ; 800 - carries 450
    [js/MOVE js/CARRY js/CARRY js/WORK js/MOVE js/CARRY js/CARRY js/WORK js/MOVE] ; 550
    [js/MOVE js/CARRY js/CARRY js/WORK js/MOVE] ; 300
    ]})
