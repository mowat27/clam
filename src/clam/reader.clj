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
  (let [*reader* (new StringReader text)]
    [(next-field *reader* delimiter)]))