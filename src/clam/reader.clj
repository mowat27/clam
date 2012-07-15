(ns clam.reader
  (:import java.io.StringReader))

(defn next-field
  ([reader delimiter] (next-field reader delimiter []))
  ([reader delimiter result]
    (let [byte-code (.read reader) character (or (= -1 byte-code) (char byte-code))]
      (cond
        (= -1 byte-code) nil
        (= delimiter (str character)) (apply str (reverse result))
        :else (recur reader delimiter (cons character result))
      ))))

(defn read-row [text delimiter]

  (defn build-field-vec [result ch]
    (let [ch-str (str ch)]
      (if (= ch-str delimiter)
        result
        (cons ch-str result))))

  [(apply str (reverse (reduce build-field-vec [] text)))]
)