(ns screeps.game
  (:refer-clojure :exclude [time])
  (:use [screeps.utils :only [jsx->clj]]))

(defn time
  "Return the game time"
  []
  (.-time js/Game))

(defn creeps
  ([]
   (-> (.-creeps js/Game)
       jsx->clj
       vals))
  ([n]
   (aget (.-creeps js/Game) n)))

(defn spawns
  ([]
   (-> (.-spawns js/Game)
       jsx->clj
       vals))
  ([n]
   (aget (.-spawns js/Game) n)))

(defn rooms
  ([]
   (-> (.-rooms js/Game)
       jsx->clj
       vals))
  ([n]
   (aget (.-rooms js/Game) n)))

(defn object
  [id]
  (.getObjectById js/Game id))

(defn used-cpu
  []
  (.getUsedCpu js/Game))

(defn cpu-limit
  []
  (.-cpuLimit js/Game))

