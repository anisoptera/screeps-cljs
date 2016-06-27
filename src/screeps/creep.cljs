(ns screeps.creep
  (:refer-clojure :exclude [name]))

(defn id
  [c]
  (.-id c))

(defn body
  [c]
  (js->clj (.-body c)))

(defn name
  [c]
  (.-name c))

(defn room
  [c]
  (.-room c))

(defn move
  [c direction]
  (.move c direction))

(defn move-to
  [c target]
  (.moveTo c target))

(defn build
  [c t]
  (.build c t))

(defn harvest
  [c t]
  (.harvest c t))

(defn energy
  [c]
  (aget c "carry" "energy"))

(defn energy-capacity
  [c]
  (aget c "carryCapacity"))

(defn transfer-energy
  [c t]
  (.transfer c t js/RESOURCE_ENERGY nil))

(defn upgrade-controller
  [c ctrl]
  (.upgradeController c ctrl))

(defn claim-controller
  [c ctrl]
  (.claimController c ctrl))

(defn ttl
  [c]
  (.-ticksToLive c))

(defn jsx->clj
  [x]
  (into {} (for [k (.keys js/Object x)] [(keyword k) (aget x k)])))

(defn memory
  [c]
  (jsx->clj (.-memory c)))

(defn memory!
  [c m]
  (aset js/Memory "creeps" (name c) (clj->js m)))

