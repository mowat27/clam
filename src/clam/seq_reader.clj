(ns clam.seq-reader)

(declare take-fixed)

(let [dispatcher (fn [_ data]
                   (cond
                     (char? (first data))             :string
                     (or (vector? data) (list? data)) :collection))]

  (defmulti  take-fixed dispatcher)
  (defmethod take-fixed :string     [len text]     (take-fixed len [[] text]))
  (defmethod take-fixed :collection [len [_ text]] (split-at len text))

  (defmulti  take-delimited dispatcher)
  (defmethod take-delimited :string     [delimiter text] (take-delimited delimiter [[] text]))
  (defmethod take-delimited :collection [delimiter [_ coll]]
    (let [delimiter (seq delimiter)
          c         (count delimiter)
          this      (->> (reductions conj [] coll)
                         (take-while #(not (= delimiter (take-last c %))))
                         last)
          len       (+ c (count this))
          remainder (drop len coll)]
      [this remainder])))

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

(defn get-field-names [record-defs]
  (map first record-defs))

(defn get-field-defs [record-defs]
  (map second record-defs))

(defn record-seq [record-defs coll]
  (let [field-names (get-field-names record-defs)
        field-defs  (get-field-defs  record-defs)]
    (for [row (row-seq field-defs coll)]
      (apply hash-map (interleave field-names row)))))

