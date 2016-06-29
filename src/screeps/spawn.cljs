(ns screeps.spawn
  (:refer-clojure :exclude [name])
  (:require [screeps.creep :as creep]
            [screeps.room :as room])
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

(defn spawning?
  [s]
  (.-spawning s))

(defn create-creep
  [sp body]
  (.createCreep sp (clj->js body) nil nil))

(defn renew-creep
  [sp creep]
  (.renewCreep sp creep))

(defn energy
  [sp]
  (.-energy sp))
