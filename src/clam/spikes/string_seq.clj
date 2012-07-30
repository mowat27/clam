(ns clam.spikes.string-seq)

(def text
  (seq "foobarbopbaz"))

(defn gen-text [num-rows]
  (apply str (repeat num-rows "foobar")))

(defn delim [d [r s]]
  [(take-while (partial not= d) s) (rest s)])

(defn fixed [len [r s]]
  [(take len s) (drop len s)])

(def chunkers
  (repeat 2 (partial fixed 3)))

(defn fields [chunkers coll]
  (defn not-eof? [[_ [row _]]] (not (empty? row)))
  (def field-seq
    (iterate
      (fn [[c r]] [(rest c) ((first c) r)])
      [(cycle chunkers) ['(:start) coll]]))
  (map (fn [[_ r]] (first r)) (rest (take-while not-eof? field-seq))))

(defn rows [chunkers coll]
  (partition (count chunkers) (fields chunkers coll)))







