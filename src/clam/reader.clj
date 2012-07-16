(ns clam.reader)

(defn equal-strings? [& args]
  (apply = (map str args)))

(defn parse-delimited [text delimiter]
  (->>
    text
    (partition-by (partial equal-strings? delimiter))
    (map (partial apply str))
    (filter (partial not= delimiter))))

(defn fixed-chunk [text size]
  [(subs text 0 size) (subs text size)])

(defn parse-fixed [text field-lengths]
  (if (empty? field-lengths)
    nil
    (let [len       (first       field-lengths)
          chunk     (fixed-chunk text len)
          field     (first       chunk)
          remainder (last        chunk) ]
      (cons field (parse-fixed remainder (rest field-lengths))))))
