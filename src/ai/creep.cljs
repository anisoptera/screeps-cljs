(ns ai.creep
  (:require [screeps.game :as game]
            [screeps.creep :as creep]
            [screeps.room :as room]
            [screeps.spawn :as spawn]
            [screeps.position :as pos]
            [screeps.structure :as structure]
            [screeps.memory :as m]))

(defn perform-at
  [creep tgt f]
  (if (pos/next-to? creep tgt)
    (f creep tgt)
    (creep/move-to creep tgt)))

(defn renew-creep
  [creep]
  (let [room (creep/room creep)
        spawns (room/find room js/FIND_MY_STRUCTURES #(= (structure/type %) js/STRUCTURE_SPAWN))
        target-spawn (game/spawns  (.-name (first spawns)))]
    (if (pos/next-to? creep target-spawn)
      (do 
        (.renewCreep target-spawn creep))
      (creep/move-to creep target-spawn))))

(defn find-empty-container
  [creep]
  (room/find-closest-by-range (structure/position creep) js/FIND_STRUCTURES
                              #(and (= (structure/type %) js/STRUCTURE_CONTAINER)
                                    (< (reduce + (vals (structure/store %))) (structure/store-capacity %)))))

(defn collect-energy
  [creep]
  (let [room (creep/room creep)
        sources (room/find room js/FIND_SOURCES_ACTIVE)
        id-fun (if (= (room/name room) "sim") ;; simulated ids are like "idxxxx00", so we only want the middle bits.
                 #(.substring % 2 4)          ;; real ids are giant hex monstrosities, and only the least significant bits are unique
                 #(.substring % 20))
        id (js/parseInt (id-fun (creep/id creep)) 16)
        source (nth sources (mod id (count sources)))
        ctrlr (room/controller room)
        const-site (room/find-closest-by-range (structure/position creep) js/FIND_CONSTRUCTION_SITES)
        empty-extension (first (room/find room js/FIND_MY_STRUCTURES #(and
                                                                             (= (structure/type %) js/STRUCTURE_EXTENSION)
                                                                             (< (structure/energy %) (structure/energy-capacity %)))))
        empty-tower (first (room/find room js/FIND_MY_STRUCTURES #(and (= (structure/type %) js/STRUCTURE_TOWER)
                                                                       (< 100 (- (structure/energy-capacity %) (structure/energy %))))))
        empty-container (find-empty-container creep)
        m (creep/memory creep)
        sp1 (first (room/find room js/FIND_MY_STRUCTURES #(= (structure/type %) js/STRUCTURE_SPAWN)))]

    (if (:dump m)
      (do
       (cond
        (and (not (nil? ctrlr)) (not (structure/mine? ctrlr)))
        (perform-at creep ctrlr creep/claim-controller)

        (= 1 (structure/level ctrlr))
        (perform-at creep ctrlr creep/upgrade-controller)

        (< (.-energy sp1) 100)
        (perform-at creep sp1 creep/transfer-energy)

        const-site
        (perform-at creep const-site creep/build)

        (< (.-energy sp1) 300)
        (perform-at creep sp1 creep/transfer-energy)

        empty-extension
        (perform-at creep empty-extension creep/transfer-energy)

        empty-tower
        (perform-at creep empty-tower creep/transfer-energy)

        empty-container
        (perform-at creep empty-container creep/transfer-energy)

        :else
        (perform-at creep ctrlr creep/upgrade-controller))
       (if (= (creep/energy creep) 0)
         (creep/memory! creep (assoc m :dump false))))
      (if (= (creep/energy creep) (creep/energy-capacity creep))
        (creep/memory! creep (assoc m :dump true))
        (perform-at creep source creep/harvest)))))

(defn run-courier
  [creep]
  (collect-energy creep))

(defn run-miner
  [creep]
  (collect-energy creep)
  #_(let [m (creep/memory creep)]
    (when (nil? (:source m))
      )))

(defn run-creep
  [creep]
  (let [role (:role (creep/memory creep))]
    (condp = role
      "miner" (run-miner creep)
      "courier" (run-courier creep)
      (collect-energy creep))))
