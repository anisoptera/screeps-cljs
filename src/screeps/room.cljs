(ns screeps.room
  (:refer-clojure :exclude [name find])
  (:require [screeps.position :as pos])
  (:use [screeps.utils :only [jsx->clj]]
        [screeps.memory :only [*memory*]]))

(defn name
  [r]
  (.-name r))

(defn mode
  [r]
  (.-mode r))

(defn memory
  [r]
  (get-in @*memory* ["rooms" (name r)] {}))

(defn memory!
  [r m]
  (swap! *memory* #(assoc-in % ["rooms" (name r)] m)))

(defn controller
  [r]
  (.-controller r))

(defn available-energy
  [r]
  (.-energyAvailable r))

(defn energy-capacity
  [r]
  (.-energyCapacityAvailable r))

(defn find
  [r otype & [ffn]]
  (array-seq (.find r otype (if ffn #js {:filter ffn}))))

(defn find-path
  [r from to & [opts]]
  (js->clj (.findPath r from to (if opts (clj->js opts))) :keywordize-keys true))

(defn find-in-range
  [from otype range & [ffn]]
  (array-seq (.findInRange (pos/position from) otype range (if ffn #js {:filter ffn}))))

(defn find-closest-by-range
  [from otype & [ffn]]
  (js->clj (.findClosestByRange (pos/position from) otype (if ffn #js {:filter ffn}))))

(defn get-closest-by-range
  [from objs & [ffn]]
  (js->clj (.findClosestByRange (pos/position from) (clj->js objs) (if ffn #js {:filter ffn}))))

(defn look
  ([r x y]
   (js->clj (.lookAt r x y) :keywordize-keys true))
  ([r tgt]
   (js->clj (.lookAt r tgt) :keywordize-keys true)))

(defn look-for
  ([r x y t]
   (js->clj (.lookForAt r t x y) :keywordize-keys true))
  ([r p t]
   (js->clj (.lookForAt r t p) :keywordize-keys true)))

(defn create-construction-site
  ([r x y t]
   (.createConstructionSite r x y t))
  ([r p t]
   (.createConstructionSite r p t)))

