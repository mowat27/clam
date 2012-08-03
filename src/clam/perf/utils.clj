(ns clam.perf.utils)

(defn gen-row [num-fields]
    (str (apply str (repeat num-fields "foobar\t")) "\n"))

(defn gen-data [num-rows num-fields]
    (apply str (repeat num-rows (gen-row num-fields))))