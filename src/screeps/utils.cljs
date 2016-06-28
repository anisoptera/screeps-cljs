(ns screeps.utils
  (:refer-clojure :exclude []))

(defn jsx->clj
  [x]
  (into {} (for [k (.keys js/Object x)] [(keyword k) (aget x k)])))
