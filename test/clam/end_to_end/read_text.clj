(ns clam.end-to-end.read-text
  (:use midje.sweet
        clam.core))

(def pipe_delim_text
"Lorem|ipsum|dolor|
sit|amet,|consectetur|
")

(def lorem-record
  (defrecstruct
    [ [:field1  {:delimiter "|"}]
      [:field2  {:delimiter "|"}]
      [:field3  {:delimiter "|"}]
      [:newline {:length     1 }]]))

(fact
  (lorem-record pipe_delim_text) => [
    {:field1 "Lorem" :field2 "ipsum" :field3 "dolor",       :newline "\n"}
    {:field1 "sit"   :field2 "amet," :field3 "consectetur", :newline "\n"} ])
