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
        (+ chunk-size ignore-after)))))

(defn delimited-chunk [delimiter text]
  (let [chunk-size (.indexOf text delimiter)]
    (fixed-chunk chunk-size (count delimiter) text)))


;; Parsers
(defn parse-text
  ([text] (parse-text [] text))
  ([chunkers text]
    (if (empty? chunkers)
      nil
      (let [chunker   (first chunkers)
            chunk     (chunker text)
            field     (first chunk)
            remainder (second chunk)]
        (cons field (parse-text (rest chunkers) remainder))))))

(defn get-chunk [chunker args]
  (if (string? args)
    (let [[field remainder length] (chunker args)]
      [[field] remainder])
    (let [[row text] args [field remainder length] (chunker text)]
      (vector
        (concat row [field]) remainder))))

(defn read-row [chunkers text]
  (reduce #(get-chunk %2 %1) (cons text chunkers)))

;; Generators
(defn chunker-for [field-args]
  (cond
    (:delimiter field-args) (partial delimited-chunk (:delimiter field-args))
    (:length    field-args) (partial fixed-chunk     (:length    field-args))
    :else nil))

(defn record-format [& field-definitions]
  (let [field-names (map first  field-definitions)
        field-args  (map second field-definitions)
        chunkers    (map chunker-for field-args)]

    (defn parse-rows [text]
      (if (empty? text)
        nil
        (let [[row remainder] (read-row chunkers text)]
          (cons
            (apply hash-map (interleave field-names row))
            (parse-rows remainder)))))

    (fn [text] (parse-rows text))
  ))
