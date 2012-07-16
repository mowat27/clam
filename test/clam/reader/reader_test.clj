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

(facts "about delimited-chunk"
  (delimited-chunk "foo,bar,baz" ",") => ["foo" "bar,baz"]
  (delimited-chunk "foo,bar,baz" ",bar,") => ["foo" "baz"]
  )

(facts "about parse-delimited"
  (parse-delimited "f1|" "|") => ["f1"]
  (parse-delimited "f1," ",") => ["f1"]
  (parse-delimited "f1,f2," ",") => ["f1" "f2"]
  )

(facts "about fixed-chunk"
  (fixed-chunk "xxxyyyzzz" 3) => ["xxx" "yyyzzz"]
  (fixed-chunk "xxxyyyzzz" 3 6) => ["xxx" ""]
  (fixed-chunk "xx" 3) => nil
  )

(facts "about parse-fixed"
  (parse-fixed "xyyzzz" [1 2 3]) => ["x" "yy" "zzz"]
  (parse-fixed "xyyzz" [1 2 3]) => ["x" "yy" nil]
  )
