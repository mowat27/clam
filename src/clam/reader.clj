(ns clam.reader)

(defn fixed-chunk
  ([text chunk-size] (fixed-chunk text chunk-size 0))
  ([text chunk-size ignore-after]
    (if (>= (count text) chunk-size)
      (vector
        (subs text 0 chunk-size)
        (subs text (+ chunk-size ignore-after))))))

(defn delimited-chunk [text delimiter]
  (let [chunk-size (.indexOf text delimiter)]
    (fixed-chunk text chunk-size (count delimiter))))

(defn parse-delimited [text delimiter]
  (if (empty? text)
    nil
    (let [chunk     (delimited-chunk text delimiter)
          field     (first       chunk)
          remainder (last        chunk) ]
      (cons field (parse-delimited remainder delimiter)))))

(defn parse-fixed [text field-lengths]
  (if (empty? field-lengths)
    nil
    (let [len       (first       field-lengths)
          chunk     (fixed-chunk text len)
          field     (first       chunk)
          remainder (last        chunk) ]
      (cons field (parse-fixed remainder (rest field-lengths))))))
