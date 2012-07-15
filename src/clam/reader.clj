(ns clam.reader)



(defn read-row [text delimiter]

  (defn x [result ch]
    (let [ch-str (str ch)]
    (if (= ch-str delimiter)
      result
      (cons ch-str result))))

  [(apply str (reverse (reduce x [] text)))]

)