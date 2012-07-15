(ns clam.reader.reader_test
  (:use midje.sweet
        clam.reader))

(fact (read-row "f1|" "|") => ["f1"])
(fact (read-row "f1," ",") => ["f1"])