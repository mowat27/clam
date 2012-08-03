(ns clam.reader.seq_reader_test
  (:use midje.sweet
        [clam.seq-reader :as rdr]))

(def fixed-row-def     (repeat 2 (partial take-fixed 3)))
(def delimited-row-def (repeat 2 (partial take-delimited (seq ","))))
(def fixed-record-def  [[:f1 (first fixed-row-def)] [:f2 (second fixed-row-def)]])

(def foo (seq "foo"))
(def bar (seq "bar"))
(def bop (seq "bop"))
(def baz (seq "baz"))

(facts "about take-fixed"
  (rdr/take-fixed 3 "foobar")        => [foo bar]
  (rdr/take-fixed 3 (seq "foobar"))  => [foo bar]
  (rdr/take-fixed 3 ['() "foobar"])  => [foo bar]
  (rdr/take-fixed 3 '('() "foobar")) => [foo bar]
  )

(facts "about take-delimited"
  (rdr/take-delimited "," "foo,bar")              => [foo bar]
  (rdr/take-delimited "," ['() "foo,bar"])        => [foo bar]
  (rdr/take-delimited (seq ",") "foo,bar")        => [foo bar]
  (rdr/take-delimited (seq ",") ['() "foo,bar"])  => [foo bar]
  (rdr/take-delimited (seq ",") '('() "foo,bar")) => [foo bar]
  )

(facts "about take-field"
  (rdr/take-field fixed-row-def "foobar")            => [(rest fixed-row-def)     [foo bar]]
  (rdr/take-field fixed-row-def (seq "foobar"))      => [(rest fixed-row-def)     [foo bar]]
  (rdr/take-field [fixed-row-def [foo bar]] )        => [(rest fixed-row-def)     [bar '()]]

  (rdr/take-field [delimited-row-def [foo "bar,"]] ) => [(rest delimited-row-def) [bar '()]]
  (rdr/take-field delimited-row-def "foo,bar")       => [(rest delimited-row-def) [foo bar]]
  (rdr/take-field delimited-row-def (seq "foo,bar")) => [(rest delimited-row-def) [foo bar]]
  )

(facts "about field-seq"
  (take 4 (rdr/field-seq fixed-row-def "foobarbopbaz"))         => [foo bar bop baz]
  (take 4 (rdr/field-seq delimited-row-def "foo,bar,bop,baz,")) => [foo bar bop baz]
  )

(facts "about row-seq"
  (take 2 (rdr/row-seq fixed-row-def "foobarbopbaz")) => [[foo bar] [bop baz]]
  )

(facts "about record-seq"
  (take 2 (rdr/record-seq fixed-record-def "foobarbopbaz")) => [{:f1 foo :f2 bar}
                                                                {:f1 bop :f2 baz}]
  )