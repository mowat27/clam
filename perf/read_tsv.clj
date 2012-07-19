(ns clam.perf.read-tsv
  (:use clam.core))

(def airport
  (defrecstruct
    (apply vector
      (map #([% {:delimiter "\t"}]) [
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
      [:newline {:length      1 }])))

(def data (slurp "tsv/dec_2011/airport.tsv"))
