(ns screeps.spawn
  (:refer-clojure :exclude [name]))

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
  (let [capacity (energy-capacity sp)]
    (.createCreep sp (clj->js (cap-template template capacity)) nil (clj->js {:role role :size capacity}))))

(def miner-template
  [js/MOVE js/WORK js/CARRY js/WORK ; 300
   js/WORK js/MOVE js/WORK]) ; 550

(defn create-miner
  [sp]
  (create-template sp miner-template "miner"))

(def courier-template
  [js/MOVE js/CARRY js/CARRY js/CARRY js/MOVE js/MOVE ; 300
   js/CARRY js/CARRY js/WORK js/MOVE] ; 550
  )

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
