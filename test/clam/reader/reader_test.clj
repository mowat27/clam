(ns clam.reader.reader_test
  (:use midje.sweet
        clam.reader))


(facts "about delimited-chunk"
  (delimited-chunk ","     "foo,bar,baz") => ["foo" "bar,baz"]
  (delimited-chunk ",bar," "foo,bar,baz") => ["foo" "baz"]
  )

(facts "about parse-delimited"
  (parse-delimited "|" "f1|")    => ["f1"]
  (parse-delimited "," "f1,")    => ["f1"]
  (parse-delimited "," "f1,f2,") => ["f1" "f2"]
  )

(facts "about fixed-chunk"
  (fixed-chunk 3 "xxxyyyzzz")   => ["xxx" "yyyzzz"]
  (fixed-chunk 3 6 "xxxyyyzzz") => ["xxx" ""]
  (fixed-chunk 3 "xx")          => nil
  )

(facts "about parse-fixed"
  (parse-fixed [1 2 3] "xyyzzz") => ["x" "yy" "zzz"]
  (parse-fixed [1 2 3] "xyyzz")  => ["x" "yy" nil]
  )

(facts "about parse"
  (parse "foo,bar")  => nil
  (parse "foo,bar"
    (partial delimited-chunk ",")
    (partial fixed-chunk      3 )) => ["foo" "bar"]
  (parse "foo,bar"
    (partial delimited-chunk ",")
    (partial fixed-chunk      3 )
    (partial fixed-chunk      3 )) => ["foo" "bar" nil]
  )
