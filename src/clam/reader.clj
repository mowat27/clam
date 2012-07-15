(ns clam.reader)

(defn tst [] "hello world")

(defn read-row [text delimiter]

  (defn build-field-vec [result ch]
    (let [ch-str (str ch)]
      (if (= ch-str delimiter)
        result
        (cons ch-str result))))

  [(apply str (reverse (reduce build-field-vec [] text)))]

)