(ns clam.reader)

(declare fixed-chunk delimited-chunk
         parse-text parse-delimited parse-fixed
         record-format chunker-for)

;; Chunkers
(defn fixed-chunk
  ([chunk-size text ] (fixed-chunk chunk-size 0 text))
  ([chunk-size ignore-after text]
    (if (>= (count text) chunk-size)
      (vector
        (subs text 0 chunk-size)
        (subs text (+ chunk-size ignore-after))
        (+ chunk-size ignore-after))
      (throw (Exception. (str "Unexpected EOF. Expected '" text "' to be at least " chunk-size " characters long.")))
    )))

(defn delimited-chunk [delimiter text]
  (let [chunk-size (.indexOf text delimiter)]
    (if (> chunk-size -1)
      (fixed-chunk chunk-size (count delimiter) text)
      (throw (Exception. (str "Unexpected EOF. Looking for '" delimiter "' in '" text "'."))))))

;; Parsers
;; ---------------------------------------------------
(defmulti read-chunk
  (fn [_ args]
    (cond
      (string? args) :string
      (coll?   args) :collection)))

(defmethod read-chunk :string [chunker text]
  (let [[field remainder length] (chunker text)]
    [[field] remainder]))

(defmethod read-chunk :collection [chunker coll]
  (let [[row text]               coll
        [field remainder length] (chunker text)]
      (vector
        (concat row [field]) remainder)))
;; ---------------------------------------------------

(defn read-row [chunkers text]
  (reduce #(read-chunk %2 %1) (cons text chunkers)))

(defn read-all-rows [chunkers text]
  (if (empty? text)
    nil
    (let [[row remainder] (read-row chunkers text)]
      (cons
        row
        (read-all-rows chunkers remainder)))))

;; Generators
(defn chunker-for [field-args]
  (cond
    (:delimiter field-args) (partial delimited-chunk (:delimiter field-args))
    (:length    field-args) (partial fixed-chunk     (:length    field-args))
    :else nil))

(defn delimited-fields [delimiter field-names]
  (concat
    (map #(vector %1 {:delimiter delimiter}) field-names)
    [[:newline {:length 1 }]]))

(defn record-format [& field-definitions]
  (let [field-names (map first  field-definitions)
        field-args  (map second field-definitions)
        chunkers    (map chunker-for field-args)]

    (fn [op & args]
      (cond
        (= op :read)
          (let [[text] args]
            (for [row (read-all-rows chunkers text)]
              (->> row (interleave field-names) (apply hash-map))))
        (= op :field-defs) field-definitions)))
    )

