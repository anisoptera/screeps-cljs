(ns ai.room
  (:require [screeps.game :as game]
            [screeps.creep :as creep]
            [screeps.room :as room]
            [screeps.spawn :as spawn]
            [screeps.position :as pos]
            [screeps.structure :as structure]
            [screeps.memory :as m])
  (:refer-clojure :exclude []))

(defn ^:export init-sources
  [room]
  (let [m (room/memory room)
        sources (room/find room js/FIND_SOURCES)
        slots (into {} (map #(vector (structure/id %) {"free" 1}) sources))]
    (room/memory! room (assoc m "sources" slots))
    slots))

(defn ^:export reset-sources
  [room]
  (let [m (room/memory room)
        creeps (room/find room js/FIND_MY_CREEPS)]
    (init-sources room)
    (doseq [creep creeps]
      (let [cm (creep/memory creep)]
        (creep/memory! creep (dissoc cm "source"))))))

(defn ^:export select-source
  [creep]
  (let [room (creep/room creep)
        m (room/memory room)
        cm (creep/memory creep)]

    (let [sources (or (get m "sources")
                      (init-sources room))
          open-sources (filter #(< 0 (get-in sources [% "free"])) (keys sources))
          my-id (creep/id creep)]
      (when-let [my-source (first (shuffle open-sources))]
        (room/memory! room (assoc m "sources"
                                  (-> sources
                                      (update-in [my-source "free"] dec)
                                      (update-in [my-source "assigned"] #(if (nil? %) [my-id] (conj % my-id))))))
        (creep/memory! creep (assoc cm "source" (name my-source)))
        my-source))))

(defn load-source
  [creep]
  (let [m (creep/memory creep)]
    (if-let [source-id (or (get m "source")
                           (select-source creep))]
      (game/object source-id)
      nil)))
