(ns clam.reader.seq_reader_test
  (:use midje.sweet
        [clam.seq-reader :as rdr]))

(def fixed-row-def (repeat 2 (partial take-fixed 3)))

(facts "about take-fixed"
  (rdr/take-fixed 3 ['() (seq "foobar")]) => ['(\f \o \o) '(\b \a \r)]
  )

(facts "about field-seq"
  (take 2 (rdr/field-seq fixed-row-def "foobar"))       => (map seq ["foo" "bar"])
  (take 4 (rdr/field-seq fixed-row-def "foobarbopbaz")) => (map seq ["foo" "bar" "bop" "baz"])
  )