(ns clam.reader)

(declare fixed-chunk delimited-chunk
         parse-text parse-delimited parse-fixed
         record-format chunker-for validate-field)

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
  "Applies a list of functions to a string. Returns
  a vector of fields found and any remaining text.
  (read-row comma-chunkers \"foo,bar,bop,baz,\")  => [[\"foo\" \"bar\"] \"bop,baz,\"]"
  (reduce #(read-chunk %2 %1) (cons text chunkers)))

(defn read-all-rows [chunkers starting-text]
  "Repeatedly applies chunkers to text until the end of the
  text is reached."
  (reverse (loop [text starting-text result []]
    (if (empty? text)
      result
      (let [[row remainder] (read-row chunkers text)]
        (recur remainder (cons row result)))))))

;; Generators
(defn chunker-for [field-args]
  (cond
    (:delimiter field-args) (partial delimited-chunk (:delimiter field-args))
    (:length    field-args) (partial fixed-chunk     (:length    field-args))
    :else (throw (Exception. (str "Expected :delimited or :length to be specified in " field-args)))))

(defn delimited-fields [delimiter field-names]
  (concat
    (map #(vector %1 {:delimiter delimiter}) field-names)
    [[:newline {:length 1 }]]))

(defn record-format [& field-definitions]
  (doall
    (for [field-def field-definitions] (validate-field field-def)))
  (let [field-names (map first  field-definitions)
        field-args  (map second field-definitions)
        chunkers    (doall (map chunker-for field-args))]

    (fn [op & args]
      (cond
        (= op :read)
          (let [[text] args]
            (for [row (read-all-rows chunkers text)]
              (->> row (interleave field-names) (apply hash-map))))
        (= op :field-defs)
          field-definitions))))

;; Validators
(defn validate-field [field-def]
  (defn vector-or-list [field-def] (or (vector? field-def) (list? field-def)))

  (assert (vector-or-list field-def) (str field-def " must be a list or vector"))
  (assert (= 2 (count field-def)) (str field-def " must have a field name and options"))

  field-def)


