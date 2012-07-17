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
(defn parse-delimited [delimiter text]
  (if (empty? text)
    nil
    (let [chunk     (delimited-chunk delimiter text)
          field     (first       chunk)
          remainder (second        chunk) ]
      (cons field (parse-delimited delimiter remainder)))))

(defn parse-fixed [field-lengths text]
  (let [chunkers (map (fn [len] (partial fixed-chunk len)) field-lengths)]
    (parse-text chunkers text)))

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

    (defn parse-row [text]
      (let [values (parse-text chunkers text)]
        (apply hash-map (interleave field-names values))))

    (defn parse-rows [text]
      (if (empty? text)
        nil
        (let [row (parse-row text)]
          (cons
            row
            (parse-rows (->> (apply str (vals row)) count inc (subs text)))))))

    (fn [text] (parse-rows text))
  ))
