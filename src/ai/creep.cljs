(ns ai.creep
  (:require [screeps.game :as game]
            [screeps.creep :as creep]
            [screeps.room :as room]
            [screeps.spawn :as spawn]
            [screeps.position :as pos]
            [screeps.structure :as structure]
            [screeps.memory :as m])
  (:refer-clojure :exclude [])
  (:use [ai.tower :only [desired-wall-strength]]))

(defn perform-at
  [creep tgt f & [dist]]
  (let [dst (or dist 1)]
    (if (pos/in-range? creep tgt dst)
      (f creep tgt)
      (creep/move-to creep tgt))))

(defn perform-with
  [creep tgt f & dist]
  (let [dst (or dist 1)]
    (if (pos/next-to? creep tgt)
      (f tgt creep)
      (creep/move-to creep tgt))))

(defn renew-creep
  [creep]
  (let [room (creep/room creep)
        spawns (room/find room js/FIND_MY_STRUCTURES #(= (structure/type %) js/STRUCTURE_SPAWN))
        target-spawn (game/spawns  (.-name (first spawns)))]
    (if (pos/next-to? creep target-spawn)
      (do
        (.renewCreep target-spawn creep))
      (creep/move-to creep target-spawn))))

(defn find-ground-score
  [creep]
  (room/find-closest-by-range creep js/FIND_DROPPED_ENERGY))

(defn find-full-container
  [creep]
  (room/find-closest-by-range creep js/FIND_STRUCTURES
                              #(and (= (structure/type %) js/STRUCTURE_CONTAINER)
                                    (< 500 (structure/store-quantity %)))))

(defn find-empty-container
  "Finds the closest empty container to creep (if any)."
  [creep]
  (room/find-closest-by-range creep js/FIND_STRUCTURES
                              #(and (= (structure/type %) js/STRUCTURE_CONTAINER)
                                    (< (structure/store-quantity %) (structure/store-capacity %)))))

(defn find-empty-extension
  "Finds the closest empty extension to creep (if any)."
  [creep]
  (room/find-closest-by-range creep js/FIND_MY_STRUCTURES #(and
                                          (= (structure/type %) js/STRUCTURE_EXTENSION)
                                          (< (structure/energy %) (structure/energy-capacity %)))))

(defn find-empty-tower
  [creep]
  (room/find-closest-by-range creep js/FIND_MY_STRUCTURES
                              #(and (= (structure/type %) js/STRUCTURE_TOWER)
                                    (< 600 (- (structure/energy-capacity %) (structure/energy %))))))

(defn find-nearest-filter
  [creep find-dir type-filter & [[ffn]]] ;;why is this a double wrapped list?...
  (room/find-closest-by-range creep find-dir
                              #(and (= (structure/type %) type-filter)
                                    (if ffn (ffn %) true))))

(defn find-storage
  [creep & ffn]
  (find-nearest-filter creep js/FIND_MY_STRUCTURES js/STRUCTURE_STORAGE ffn))

(def task-map
  {
   'creep/claim-controller [creep/claim-controller 1]
   'creep/upgrade-controller [creep/upgrade-controller 3]
   'creep/transfer-energy [creep/transfer-energy 1]
   'creep/build [creep/build 3]
   })

(defn cache-task
  [creep tgt sym]
  (let [m (creep/memory creep)
        [fun dist] (task-map sym)]
    (creep/memory! creep (assoc m "task" [(structure/id tgt) sym]))
    (perform-at creep tgt fun dist)))

(defn perform-cached-task
  [creep [tgt-id sym]]
  (if-let [tgt (game/object tgt-id)]
    (let [[fun dist] (task-map sym)
          result (perform-at creep tgt fun dist)]
      (when-not (or (= 0 result)
                    (= js/ERR_TIRED result))
        (creep/memory! creep (dissoc (creep/memory creep) "task"))))
    (creep/memory! creep (dissoc (creep/memory creep) "task"))))

(defn find-driveby-repair-target
  [creep]
  (let [m (creep/memory creep)]
    (if (= 0 (mod (game/time) 6))
      (let [rep-target (first (room/find-in-range creep js/FIND_STRUCTURES 3
                                                  #(< 100 (- (structure/max-hits %) (structure/hits %)))))]
        (if rep-target
          (creep/memory! creep (assoc m "driveby-target" (structure/id rep-target)))
          (creep/memory! creep (dissoc m "driveby-target")))
        rep-target)
      (if-let [target-id (m "driveby-target")]
        (game/object target-id)
        nil))))

(defn do-driveby-repair
  [creep]
  (when-let [rep-target (find-driveby-repair-target creep)]
    (if (= js/STRUCTURE_WALL (structure/type rep-target))
      (when (> desired-wall-strength (structure/hits rep-target))
        (let [result (creep/repair creep rep-target)]
          (when-not (= 0 result)
            (creep/memory! creep (dissoc (creep/memory creep) "driveby-target")))))

      ;; not a wall, don't cap repairs
      (creep/repair creep rep-target))))

(defn collect-energy
  [creep]
  (let [room (creep/room creep)
        ctrlr (room/controller room)
        empty-extension (memoize find-empty-extension)
        const-site (memoize #(room/find-closest-by-range creep js/FIND_CONSTRUCTION_SITES))
        empty-tower (memoize find-empty-tower)
        storage (memoize find-storage)
        m (creep/memory creep)
        sp1 (first (room/find room js/FIND_MY_STRUCTURES #(= (structure/type %) js/STRUCTURE_SPAWN)))
        is-courier (= (m "arch") "courier")
        is-miner (= (m "arch") "miner")]
    (if (m "dump")
      (do
        (if-let [task (m "task")]
          (perform-cached-task creep task)

          ;; find a new task
          (do
            (cond
              (and (not (nil? ctrlr)) (not (structure/mine? ctrlr)))
              (cache-task creep ctrlr 'creep/claim-controller)

              (= 1 (structure/level ctrlr))
              (cache-task creep ctrlr 'creep/upgrade-controller)

              (< (.-energy sp1) 100)
              (cache-task creep sp1 'creep/transfer-energy)

              (const-site)
              (cache-task creep (const-site) 'creep/build)

              (and is-courier (< (.-energy sp1) 300))
              (cache-task creep sp1 'creep/transfer-energy)

              (and is-courier (empty-extension creep))
              (cache-task creep (empty-extension creep) 'creep/transfer-energy)

              (and is-courier (empty-tower creep))
              (cache-task creep (empty-tower creep) 'creep/transfer-energy)

              (and is-courier (storage creep))
              (cache-task creep (storage creep) 'creep/transfer-energy)

              :else
              (cache-task creep ctrlr 'creep/upgrade-controller))))

        ;; reset if empty
        (if (= (creep/energy creep) 0)
          (creep/memory! creep (-> m
                                   (dissoc "dump")
                                   (dissoc "task")   ;; clear task cache
                                   (dissoc "role"))) ;; after we're done, return to previous role

          ;; drive by repairs
          (do-driveby-repair creep)))

      (if (= (creep/energy creep) (creep/energy-capacity creep))
        (creep/memory! creep (assoc m "dump" true))
        ;; find something
        (let [ground-score (memoize find-ground-score)
              container (memoize find-full-container)
              storage (memoize (fn [c]
                                 (find-storage c #(< (creep/energy-capacity c) (structure/store-quantity %)))))]
          (cond
            (and (= (m "arch") "miner") (storage creep))
            (perform-with creep (storage creep) structure/transfer-energy)

            (ground-score creep)
            (perform-at creep (ground-score creep) creep/pickup)

            (container creep)
            (perform-with creep (container creep) structure/transfer-energy)

            :else ;;probably bootstrapping, just try to mine
            (let [source (room/find-closest-by-range creep js/FIND_SOURCES_ACTIVE)]
              (perform-at creep source creep/harvest))))))))

(defn run-courier
  [creep]
  (collect-energy creep))

(defn ^:export open-adjacent-tiles
  [pos]
  (for [x-mod [-1 0 1]
        y-mod [-1 0 1]
        :when (not (and (= x-mod 0) (= y-mod 0)))
        :let [x (+ (.-x pos) x-mod)
              y (+ (.-y pos) y-mod)
              check-pos (pos/create x y (pos/room-name pos))]
        :when (not (= "wall" (pos/look-for check-pos js/LOOK_TERRAIN)))]
    check-pos))

(defn ^:export init-sources
  [room]
  (let [m (room/memory room)
        sources (room/find room js/FIND_SOURCES)
        slots (into {} (map #(vector (structure/id %) {"free" (count (open-adjacent-tiles (pos/position %)))}) sources))]
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

(defn run-miner
  [creep]
  (let [m (creep/memory creep)]
    (if (= (creep/energy creep) (creep/energy-capacity creep))
      ;; find a place to dump it
      (if-let [container (find-empty-container creep)]
        (if (> 5 (pos/range-to creep container))
          (perform-at creep container creep/transfer-energy)
          (creep/drop-energy creep))
        (creep/drop-energy creep))
      ;; just mine
      (let [source (load-source creep)]
        (if-not (or (nil? source)
                    (= 0 (structure/energy source)))
          (perform-at creep source creep/harvest)

          ;; just go perform tasks i guess
          (do (creep/memory! creep (assoc m "role" "courier"))
              (run-courier creep)))))))

(defn should-renew?
  [creep]
  (let [m (creep/memory creep)]
    (cond
      (m "dying")
      false

      (and (not (m "renewing")) (< 200 (creep/ttl creep)))
      false

      (and (m "renewing") (< 1000 (creep/ttl creep)))
      (do
        (creep/memory! creep (dissoc m "renewing"))
        false)

      (m "renewing")
      true

      :else
      (let [room (creep/room creep)
            spawn (first (room/find room js/FIND_MY_SPAWNS))
            size (or (get m "size")
                     (spawn/body-cost (map #(get % :type) (creep/body creep))))]
        (if (= size (room/energy-capacity room))
          (do (creep/memory! creep (assoc m "renewing" true))
              true)
          (do (creep/memory! creep (assoc m "dying" true))
              false))))))

(defn run-creep
  [creep]
  (when-not (creep/spawning? creep)
    (let [mem (creep/memory creep)
          role (or (mem "role")
                   (mem "arch"))]

      (if (should-renew? creep)
        (renew-creep creep)

        (condp = role
          "miner" (run-miner creep)
          "courier" (run-courier creep)
          (collect-energy creep))))))
