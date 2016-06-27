(ns screeps.structure
  (:refer-clojure :exclude [type]))


(defn mine?
  [s]
  (.-my s))

(defn hits
  [s]
  (.-hits s))

(defn max-hits
  [s]
  (.-hitsMax s))

(defn id
  [s]
  (.-id s))

(defn position
  [s]
  (.-pos s))

(defn room
  [s]
  (.-room s))

(defn type
  [s]
  (.-structureType s))

(defn destroy
  [s]
  (.destroy s))

(defn energy
  [s]
  (.-energy s))

(defn energy-capacity
  [s]
  (.-energyCapacity s))

(defn transfer-energy
  [s tgt & [amt]]
  (.transferEnergy s tgt amt))

(defn cooldown
  [s]
  (.-cooldown s))

(defn level
  [s]
  (.-level s))

(defn progress
  [s]
  (.-progress s))

(defn progress-total
  [s]
  (.-progressTotal s))

(defn ticks-to-downgrade
  [s]
  (.-ticksToDowngrade s))

(defn ticks-to-live
  [s]
  (.-ticksToLive s))

(defn store
  [s]
  (.-store s))

(defn store-capacity
  [s]
  (.-storeCapacity s))

