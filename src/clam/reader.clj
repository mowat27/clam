(ns clam.reader)

(defn equal-strings? [& args]
  (apply = (map str args)))

(defn parse-delimited [text delimiter]
  (->>
    text
    (partition-by (partial equal-strings? delimiter))
    (map (partial apply str))
    (filter (partial not= delimiter))))

(defn fixed-chunk [text chunk-size]
  (if (>= (count text) chunk-size)
    [(subs text 0 chunk-size) (subs text chunk-size)]))

(defn delimited-chunk [text delimiter]
  (let [chunk-size (.indexOf text delimiter)]
    [(subs text 0 chunk-size) (subs text (inc chunk-size))]
  ))

(defn parse-fixed [text field-lengths]
  (if (empty? field-lengths)
    nil
    (let [len       (first       field-lengths)
          chunk     (fixed-chunk text len)
          field     (first       chunk)
          remainder (last        chunk) ]
      (cons field (parse-fixed remainder (rest field-lengths))))))
