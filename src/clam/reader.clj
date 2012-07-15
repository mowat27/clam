(ns clam.reader)

(defn tst [] "hello world")

(defn read-row [text delimiter]

  (defn read-field [result ch]
    (let [ch-str (str ch)]
      (if (= ch-str delimiter)
        result
        (cons ch-str result))))

  [(apply str (reverse (reduce read-field [] text)))]

)