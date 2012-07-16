(ns clam.reader)

(defn equal-strings? [& args]
  (apply = (map str args)))

(defn read-row [text delimiter]
  (filter (partial not= delimiter)
    (map (partial apply str)
      (partition-by (partial equal-strings? delimiter) text))))

