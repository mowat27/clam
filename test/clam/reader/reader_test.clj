(ns clam.reader.reader_test
  (:use midje.sweet
        clam.reader)
  (:import java.io.StringReader))

(defn reader [] (new StringReader "foo,bar,"))

(facts "about next-field"
  (next-field (reader) ",") => "foo"
  (next-field (reader) "|") => nil)

(facts "about read-row"
  (read-row "f1|" "|") => ["f1"]
  (read-row "f1," ",") => ["f1"]
  ; (read-row "f1,f2," ",") => ["f1" "f2"]
)
