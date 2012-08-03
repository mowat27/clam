(ns clam.perf.read-tsv
  (:use clam.core
        clam.reader
        clam.perf.utils
        [clam.examples.airport :as src-data]))

(def airport-fields
    (delimited-fields "\t" src-data/field-names))

(def airport (defrecstruct airport-fields))

(defn airport-data [num-rows]
    (gen-data num-rows 15))

(defn timed-run [num-rows]
    (fn [] (let [d (airport-data num-rows)]
        (time (airport :read d)) nil)))

(defn run-rows [num-rows]
    ((timed-run num-rows)))

(defn take-timings [start-at num-iterations factor]
    (map
        (fn [num-rows] {:num-rows num-rows :timing (with-out-str (run-rows num-rows))})
        (take num-iterations (iterate (partial * factor) start-at))))

(defn add-msec [m]
    (let [{timing :timing} m msecs (first (re-seq #"\d+\.\d+" timing))]
        (assoc m :msecs (bigdec msecs))))

(defn format-timing [m] (str (:num-rows m) " rows took " (:msecs m)))


;; Timings - 27 July
;
; user=> (run-rows 100)
; "Elapsed time: 248.158 msecs"
; nil
; user=> (run-rows 1000)
; "Elapsed time: 641.242 msecs"
; nil
; user=> (run-rows 10000)
; "Elapsed time: 4463.913 msecs"
; nil
; user=> (run-rows 100000)
; Bye for now!
