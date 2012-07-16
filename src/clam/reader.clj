(ns clam.reader)

(defn equal-strings? [& args]
  (apply = (map str args)))

(defn parse-delimited [text delimiter]
  (->>
    text
    (partition-by (partial equal-strings? delimiter))
    (map (partial apply str))
    (filter (partial not= delimiter))))
