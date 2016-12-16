(ns editor-clj.utils
  (:require [editor-clj.doubly-linked-list :refer :all]
            [clojure.string :as s]))

(defn str-insert
  "Insert c in string s at index i."
  [s c i]
  (str (subs s 0 i) c (subs s i)))

(defn str-replace
  "Replaces a char at the index i of s for c"
  [s c i]
  (str (subs s 0 i) c (subs s (inc i) (.length s))))

(defn str-delete
  "Deletes a char at a given index"
  [s i]
  (str (subs s 0 i) (subs s (inc i) (.length s))))

(defn slice-seq [seq start end]
  (if (and (> start 0)
           (>= (count seq) end start))
    (->> seq (drop start) (take (inc (- end start))))
    seq))

(defn merge-lines
  "l is supposed to be a list of strings and the next line will be
  concatenated with the current one." 
  [l]
  (let [nstr (str (curr l) (pick-next l))]
    (-> (pop-next l) 
        (set-curr nstr))))

(defn breakline-at
  "Breaks :curr at index i, updates :next and :curr. l is a doubly-linked 
  list"
  [l i]
  (-> l
      (add-before (.substring (curr l) 0 i))
      (set-curr (.substring (curr l) i))))

(defn lines-as-string
  [{:keys [nexts prevs curr] :as l}]
  (letfn [(acc+l [acc l]
            (str acc l \newline))]
    (reduce
     acc+l
     (reduce acc+l "" (reverse prevs))
     (conj nexts curr))))


(defn lines-as-seq
  [{:keys [nexts prevs curr] :as l}]
  (concat (reverse prevs) (conj nexts curr)))

