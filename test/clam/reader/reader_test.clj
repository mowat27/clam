(ns clam.reader.reader_test
  (:use midje.sweet
        clam.reader))

(facts "about equal-strings?"
  (equal-strings? "x" "x") => true
  (equal-strings? "x" \x) => true
  (equal-strings? \x  "x") => true
  (equal-strings? \x  "x" "x") => true
  (equal-strings? "x" "y") => false
  )

(facts "about read-row"
  (read-row "f1|" "|") => ["f1"]
  (read-row "f1," ",") => ["f1"]
  (read-row "f1,f2," ",") => ["f1" "f2"]
)
