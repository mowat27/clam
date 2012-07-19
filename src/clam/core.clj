(ns clam.core
  (:require [clam.reader :as rdr]))

(defn defrecstruct [record-structure] (apply rdr/record-format record-structure))
  ; (fn [text]
  ;   [{:field1 "Lorem" :field2 "ipsum" :field3 "dolor",       :newline "\n"}
  ;    {:field1 "sit"   :field2 "amet," :field3 "consectetur", :newline "\n"}]))


(defn -main
  "I don't do a whole lot."
  [& args]
  (println "Hello, World!"))
