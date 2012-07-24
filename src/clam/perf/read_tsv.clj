(ns clam.perf.read-tsv
  (:use clam.core))

(def airport-fields (into [] (concat
      (map #(vector %1 {:delimiter "\t"}) [
        :name
        :id
        :airport_type
        :serves
        :number_of_runways
        :number_of_passengers
        :amount_of_cargo
        :number_of_aircraft_movements
        :hub_for
        :focus_city_for
        :operator
        :runway_information
        :airlines
        :icao
        :iata ])
      [[:newline {:length 1 }]])))

(def airport (defrecstruct airport-fields))

(defn gen-row [num-fields]
    (str (apply str (repeat num-fields "foobar\t")) "\n"))

(defn gen-data [num-rows num-fields]
    (apply str (repeat num-rows (gen-row num-fields))))

(defn airport-data [num-rows]
    (gen-data num-rows 15))