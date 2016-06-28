(ns screeps.memory
  (:require [cognitect.transit :as t]))

(def *memory* (atom {}))

(defn ^:export load-memory
  []
  (reset! *memory*
          (let [r (t/reader :json)]
            (t/read r (.get js/RawMemory)))))

(defn ^:export write-memory!
  []
  (let [w (t/writer :json)]
    (.set js/RawMemory (t/write w @*memory*))))

(defn fetch
  ([]
   (@*memory*))
  ([k]
   (fetch k nil))
  ([k default]
   (if-let [m (aget js/Memory (name k))]
     (js->clj m :keywordize-keys true)
     default)))

(defn store!
  [k o]
  (swap! *memory* #(assoc % (name k) o)))

(defn update!
  "call f with memory location k and store the result back in k"
  [k f & args]
  (let [d (fetch k)]
    (store! k (apply f d args))))

