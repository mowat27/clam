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

(facts "about parse-delimited"
  (parse-delimited "f1|" "|") => ["f1"]
  (parse-delimited "f1," ",") => ["f1"]
  (parse-delimited "f1,f2," ",") => ["f1" "f2"]
  )

(facts "about fixed-chunk"
  (fixed-chunk "xxxyyyzzz" 3) => ["xxx" "yyyzzz"]
  )

(facts "about parse-fixed"
  (parse-fixed "xyyzzz" [1 2 3]) => ["x" "yy" "zzz"]
  (parse-fixed "xyyzzz" [1 2 3]) => ["x" "yy" "zzz"]
  )
