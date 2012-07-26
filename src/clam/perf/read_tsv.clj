(ns clam.perf.read-tsv
  (:use clam.core
        clam.reader))

(def airport-fields
    (delimited-fields "\t" [:name :id :airport_type :serves :number_of_runways :number_of_passengers
                            :amount_of_cargo :number_of_aircraft_movements :hub_for :focus_city_for
                            :operator :runway_information :airlines :icao :iata]))

(def airport (defrecstruct airport-fields))

(defn gen-row [num-fields]
    (str (apply str (repeat num-fields "foobar\t")) "\n"))

(defn gen-data [num-rows num-fields]
    (apply str (repeat num-rows (gen-row num-fields))))

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
