(ns clam.reader.reader_test
  (:use midje.sweet
        clam.reader))

(facts "about read-row"
  (read-row "f1|" "|") => ["f1"]
  (read-row "f1," ",") => ["f1"]
  (read-row "f1,f2," ",") => ["f1"]
)
