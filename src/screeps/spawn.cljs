(ns screeps.spawn
  (:refer-clojure :exclude [name])
  (:require [screeps.creep :as creep])
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

(defn create-template
  [sp template role]
  (let [capacity (energy-capacity sp)
        new-creep (.createCreep sp (clj->js (cap-template template capacity)) nil nil)]
    (when (string? new-creep)
      (swap! *memory* #(assoc-in % ["creeps" new-creep] {"arch" role "size" capacity})))))


(def miner-template
  [js/MOVE js/WORK js/CARRY js/WORK ; 300
   js/WORK js/MOVE js/WORK ; 550
   js/WORK js/CARRY js/WORK ; 800 - at which point we should have roads
   ])

(defn create-miner
  [sp]
  (create-template sp miner-template "miner"))

(def courier-template
  [js/MOVE js/CARRY js/CARRY js/WORK js/MOVE ; 300
   js/CARRY js/CARRY js/WORK js/MOVE ; 550
   js/CARRY js/CARRY js/CARRY js/CARRY js/CARRY ; 800 - carries 500
   ])

(defn create-courier
  [sp]
  (create-template sp courier-template "courier"))

(defn create-creep
  [sp body]
  (.createCreep sp (clj->js body) nil nil))

(defn renew-creep
  [sp creep]
  (.renewCreep sp creep))

(defn energy
  [sp]
  (.-energy sp))
