(ns clam.reader.reader_test
  (:use midje.sweet
        clam.reader))

(def comma-chunker   (partial delimited-chunk ","))
(def newline-chunker (partial delimited-chunk "\n"))
(def comma-chunkers  (repeat 2 comma-chunker))
(def csv-chunkers    [comma-chunker newline-chunker])

;; Chunkers
(facts "about delimited-chunk"
  (delimited-chunk ","     "foo,bar,baz") => ["foo" "bar,baz", 4]
  (delimited-chunk ",bar," "foo,bar,baz") => ["foo" "baz", 8]
  (delimited-chunk "\n" "foo\n") => ["foo" "", 4]
  (delimited-chunk "\n" "foo
") => ["foo" "", 4]
  (delimited-chunk "," "delimiter-missing") => (throws Exception #"Unexpected EOF. Looking for ',' in 'delimiter-missing'")
  )

(facts "about fixed-chunk"
  (fixed-chunk 3 "xxxyyyzzz")   => ["xxx" "yyyzzz" 3]
  (fixed-chunk 3 6 "xxxyyyzzz") => ["xxx" "" 9]
  (fixed-chunk 3 "xx") => (throws Exception #"Unexpected EOF. Expected 'xx' to be at least 3 characters long")
  )

;; Parsers
(facts "about read-chunk"
  (read-chunk comma-chunker   "foo,bar,bop,baz,")        => [["foo"] "bar,bop,baz,"]
  (read-chunk comma-chunker   [["foo"] "bar,bop,baz,"])  => [["foo" "bar"] "bop,baz,"]
  (read-chunk newline-chunker "foo\nbar,bop,baz,")       => [["foo"] "bar,bop,baz,"]
  (read-chunk newline-chunker [["foo"] "bar\nbop,baz,"]) => [["foo" "bar"] "bop,baz,"]
  )

(facts "about read-row"
  (read-row comma-chunkers "foo,bar,")           => [["foo" "bar"] , ""]
  (read-row comma-chunkers "foo,bar,bop,baz,")   => [["foo" "bar"] "bop,baz,"]
  (read-row csv-chunkers   "foo,bar\nbop,baz\n") => [["foo" "bar"] "bop,baz\n"]

  (read-row [csv-chunkers  [] "foo,bar\nbop,baz\n" true ])             => [csv-chunkers [["foo" "bar"]] "bop,baz\n" true ]
  (read-row [csv-chunkers  [["foo" "bar"]] "bop,baz\n" true ])         => [csv-chunkers [["foo" "bar"] ["bop" "baz"]] "" true ]
  (read-row [csv-chunkers  [["foo" "bar"] ["bop" "baz"]] "" false] )   => [csv-chunkers [["foo" "bar"] ["bop" "baz"] []] "" false ]
  )

(facts "about read-all-rows"
  (read-all-rows comma-chunkers "foo,bar,bop,baz,")   => [["foo" "bar"] ["bop" "baz"]]
  (read-all-rows csv-chunkers   "foo,bar\nbop,baz\n") => [["foo" "bar"] ["bop" "baz"]]
  )


;; Format definitions
(facts "about record-format generator"
  (record-format [:foo {:bad "arg"}]) => (throws Exception)
  (record-format "some bad def")      => (throws java.lang.AssertionError)
  )

(facts "about record-format functions"
  (def rf (record-format
            [:f1 {:delimiter ","}]
            [:f2 {:length     3 }]))

  (def csv-format (record-format
            [:f1 {:delimiter ","}]
            [:f2 {:delimiter "\n" }]))

  (rf         :read "foo,bar")            => [{:f1 "foo" :f2 "bar"}]
  (rf         :read "foo,barbop,baz")     => [{:f1 "foo" :f2 "bar"} {:f1 "bop" :f2 "baz"}]
  (csv-format :read "foo,bar\nbop,baz\n") => [{:f1 "foo" :f2 "bar"} {:f1 "bop" :f2 "baz"}]

  (rf :field-defs) => [[:f1 {:delimiter ","}] [:f2 {:length 3}]]
  )

(facts "about delimited-fields"
  (delimited-fields "," [:foo :bar]) => [ [:foo {:delimiter ","}]
                                          [:bar {:delimiter ","}]
                                          [:newline {:length 1}]]
  )

;; Validations
(fact (chunker-for {:blah 99}) => (throws Exception #"Expected :delimited or :length"))
(facts "about validate-field"
  (validate-field [:valid-field {:delimited ","}])  => [:valid-field {:delimited ","}]
  (validate-field '(:valid-field {:delimited ","})) => '(:valid-field {:delimited ","})
  (validate-field "not a collection")               => (throws java.lang.AssertionError #"must be a list or vector")
  (validate-field [{:foo 1 :bar 2}])                => (throws java.lang.AssertionError #"must have a field name and options")
  (validate-field [:foo])                           => (throws java.lang.AssertionError #"must have a field name and options")
  )

