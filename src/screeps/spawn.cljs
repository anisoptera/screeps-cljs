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
  [template max-cost]
  (if (<= (body-cost template) max-cost)
    template
    (cap-template (butlast template) max-cost)))

(def templates
  {"miner"
   [js/MOVE js/WORK js/CARRY js/WORK ; 300
    js/WORK js/MOVE js/WORK ; 550
    js/WORK js/CARRY js/MOVE js/MOVE; 800 
    ]

   "courier"
   [js/MOVE js/CARRY js/CARRY js/WORK js/MOVE ; 300
    js/CARRY js/CARRY js/WORK js/MOVE ; 550
    js/CARRY js/CARRY js/CARRY js/CARRY js/CARRY ; 800 - carries 450
    js/CARRY js/MOVE js/MOVE js/MOVE js/WORK js/WORK js/MOVE ; 1300 - carries 500, moves 1/tick on roads, has 4 WORKs
    ]})

(defn create-template
  [sp role]
  (let [template (templates role)
        capacity (room/energy-capacity (room sp))
        new-creep (.createCreep sp (clj->js (cap-template template capacity)) nil nil)]
    (when (string? new-creep)
      (swap! *memory* #(assoc-in % ["creeps" new-creep] {"arch" role "size" capacity})))))

(defn create-miner
  [sp]
  (create-template sp "miner"))

(defn create-courier
  [sp]
  (create-template sp "courier"))

(defn create-creep
  [sp body]
  (.createCreep sp (clj->js body) nil nil))

(defn renew-creep
  [sp creep]
  (.renewCreep sp creep))

(defn energy
  [sp]
  (.-energy sp))
