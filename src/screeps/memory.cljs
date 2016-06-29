(ns screeps.memory
  (:require [screeps.game :as game]
            [cognitect.transit :as t]))

(def *memory* (atom {}))

(defn cleanup-memory
  [mem]
  (-> mem
      (assoc "creeps" (into {} (filter #(game/creeps (first %)) (mem "creeps"))))))

(defn ^:export run-memory-cleanup
  []
  (swap! *memory* cleanup-memory))

(defn ^:export load-memory
  []
  (reset! *memory*
          (let [r (t/reader :json)]
            (t/read r (.get js/RawMemory))))
  (when (= 0 (mod (game/time) 100))
    (.log js/console "Running memory cleanup...")
    (run-memory-cleanup)
    (.log js/console "done." @*memory*)))

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

