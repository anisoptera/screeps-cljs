(ns screeps.memory)

(defn fetch
  ([]
   (js->clj js/Memory))
  ([k]
   (fetch k nil))
  ([k default]
   (if-let [m (aget js/Memory (name k))]
     (js->clj m :keywordize-keys true)
     default)))

(defn store!
  [k o]
  (aset js/Memory (name k) (clj->js o)))

(defn update!
  "call f with memory location k and store the result back in k"
  [k f & args]
  (let [d (fetch k)]
    (store k (apply f d args))))

