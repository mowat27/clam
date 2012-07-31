(ns clam.reader.seq_reader_test
  (:use midje.sweet
        [clam.seq-reader :as rdr]))

(def fixed-row-def    (repeat 2 (partial take-fixed 3)))
(def fixed-record-def [[:f1 (first fixed-row-def)] [:f2 (second fixed-row-def)]])

(def foo (seq "foo"))
(def bar (seq "bar"))
(def bop (seq "bop"))
(def baz (seq "baz"))

(facts "about take-fixed"
  (rdr/take-fixed 3 ['() (seq "foobar")]) => [foo bar]
  )

(facts "about take-field"
  (rdr/take-field [fixed-row-def [foo bar]] ) => [(rest fixed-row-def) [bar '()]]
  )

(facts "about field-seq"
  (take 2 (rdr/field-seq fixed-row-def "foobar"))       => [foo bar]
  (take 4 (rdr/field-seq fixed-row-def "foobarbopbaz")) => [foo bar bop baz]
  )

(facts "about row-seq"
  (take 2 (rdr/row-seq fixed-row-def "foobarbopbaz")) => [[foo bar] [bop baz]]
  )

(facts "about record-seq"
  (take 2 (rdr/record-seq fixed-record-def "foobarbopbaz")) => [{:f1 foo :f2 bar}
                                                                {:f1 bop :f2 baz}]
  )