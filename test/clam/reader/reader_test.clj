(ns clam.reader.reader_test
  (:use midje.sweet
        clam.reader))

;; Chunkers
(facts "about delimited-chunk"
  (delimited-chunk ","     "foo,bar,baz") => ["foo" "bar,baz", 4]
  (delimited-chunk ",bar," "foo,bar,baz") => ["foo" "baz", 8]
  )

(facts "about fixed-chunk"
  (fixed-chunk 3 "xxxyyyzzz")   => ["xxx" "yyyzzz" 3]
  (fixed-chunk 3 6 "xxxyyyzzz") => ["xxx" "" 9]
  (fixed-chunk 3 "xx")          => nil
  )

;; Parsers
(facts "about parse-text"
  (parse-text "foo,bar")            => nil
  (parse-text [
    (partial delimited-chunk ",")
    (partial fixed-chunk      3 )] "foo,bar" ) => ["foo" "bar"]
  (parse-text [
    (partial delimited-chunk ",")
    (partial fixed-chunk      3 )
    (partial fixed-chunk      3 )] "foo,bar" ) => ["foo" "bar" nil]
  )

(facts "about read-row"
  (def chunkers [(partial delimited-chunk ",") (partial delimited-chunk ",")])
  (read-row chunkers "foo,bar,") => [["foo" "bar"] , ""]
  (read-row chunkers "foo,bar,bop,baz,") => [["foo" "bar"] , "bop,baz,"]
  )

(facts "about get-chunk"
  (def chunker (partial delimited-chunk ","))
  (get-chunk chunker
    "foo,bar,bop,baz,")        => [["foo"] "bar,bop,baz,"]
  (get-chunk chunker
    [["foo"] "bar,bop,baz,"])  => [["foo" "bar"] "bop,baz,"]
  )

;; Format definitions
(facts "about record-format"
  (def rf (record-format
            [:f1 {:delimiter ","}]
            [:f2 {:length     3 }]))

  (rf "foo,bar")        => [{:f1 "foo" :f2 "bar"}]
  (rf "foo,barbop,baz") => [{:f1 "foo" :f2 "bar"} {:f1 "bop" :f2 "baz"}]
  )

(fact (chunker-for {:blah 99}) => nil)

