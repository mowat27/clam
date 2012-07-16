(ns clam.reader)

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
  (if (empty? field-lengths)
    nil
    (let [len       (first       field-lengths)
          chunk     (fixed-chunk len text)
          field     (first       chunk)
          remainder (last        chunk) ]
      (cons field (parse-fixed (rest field-lengths) remainder)))))

(defn parse-text [text & chunkers]
  (if (empty? chunkers)
    nil
    (let [chunker   (first chunkers)
          chunk     (chunker text)
          field     (first chunk)
          remainder (last chunk)]
      (cons field (apply parse-text remainder (rest chunkers))))))
