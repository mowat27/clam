(ns clam.seq-reader)

(defn take-fixed [len [_ text]]
  [(take len text) (drop len text)])

(defn field-seq [field-defs coll]
  (defn f [[fdefs [field remainder]]]
    [(rest fdefs) ((first fdefs) [field remainder])])

  (map (fn [[_ [field _]]] field) (rest (iterate f [(cycle field-defs) [[] coll]]))))

