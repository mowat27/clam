(ns clam.reader)

(declare parse-text)

(defn fixed-chunk
  ([chunk-size text ] (fixed-chunk chunk-size 0 text))
  ([chunk-size ignore-after text]
    (if (>= (count text) chunk-size)
      (vector
        (subs text 0 chunk-size)
        (subs text (+ chunk-size ignore-after))))))

(defn delimited-chunk [delimiter text]
  (let [chunk-size (.indexOf text delimiter)]
    (fixed-chunk chunk-size (count delimiter) text)))

(defn parse-delimited [delimiter text]
  (if (empty? text)
    nil
    (let [chunk     (delimited-chunk delimiter text)
          field     (first       chunk)
          remainder (last        chunk) ]
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
            remainder (last chunk)]
        (cons field (parse-text (rest chunkers) remainder))))))
