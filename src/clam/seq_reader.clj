(ns clam.seq-reader)

(defn take-fixed [len [_ text]]
  [(take len text) (drop len text)])

(defn take-field [[field-defs [field remainder]]]
  (let [take-partial (first field-defs)]
    [ (rest field-defs) (take-partial [field remainder]) ]))

(defn field-seq [field-defs coll]
  (def field-value (fn [[_ [field _]]] field))
  (let [inf-field-defs (cycle field-defs)
        first-field    ((first inf-field-defs) [[] coll])]
   (map field-value (iterate take-field [(rest inf-field-defs) first-field]))))

(defn row-seq [field-defs coll]
  (partition (count field-defs) (field-seq field-defs coll)))

(defn record-seq [record-defs coll]
  (let [field-names (map first record-defs)
        field-defs (map second record-defs)]
    (for [row (row-seq field-defs coll)]
      (apply hash-map (interleave field-names row)))))

