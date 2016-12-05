(ns editor-clj.structures
  (:require [clojure.string :as s]))

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

(defn line
  "Returns a atom containing map representing a line"
  [s previous-line next-line]
  (atom {:content s 
         :next next-line
         :prev previous-line}))

(defn ln-next
  ([ln]
   (:next @ln)) 
  ([ln nln]
   (reset! (ln-next ln) nln)))

(defn ln-prev
  ([ln]
   (:prev @ln))
  ([ln nln]
   (reset! (ln-prev ln) nln)))

(defn ln-content
  ([ln]
   (:content @ln))
  ([ln f & args]
   (apply swap! (into [ln] args))))

(defn ln-insert-char
  "Inserts c into the line :content at the index i"
  [ln c i] 
  (swap! ln update :content #(str-insert % i c)))

(defn ln-replace-char
  "Replaces a char at the index i of line for c"
  [ln c i]
  (swap! update ln :content #(str-replace % i c)))

(defn ln-delete-char
  "Deletes the char of the i index"
  [ln i]
  (swap! update ln :content #()))

(defn append-line
  "Updates :next field from line"
  [ln nln]
  (swap! update ln :next nln))

(defn append-line-before
  "Updates :prev field from line"
  [ln nln]
  (swap! update ln :prev nln))

(defn merge-lines
  "Concats the ln's :content with oln's and sets ln's :next to 
  oln's"
  ([ln oln] 
   (let [tln (transient @ln)
         oln @oln
         s1 (:content tln)
         s2 (:content oln)
         st (str s1 s2)]   
     (do (assoc! tln :content st)
         (assoc! tln :next (:next oln))
         (reset! ln (persistent! tln))
         ln)))
  ([ln]
   (merge-lines ln (ln-next ln))))

(defn breakline-at
  "Breaks the line at index i and updates :next"
  [ln i]
  (let [n-str (subs (ln-content ln) 0 i)
        b-str (subs (ln-content ln) i)
        old-nln (ln-next ln)

        ln-c (transient @ln)]
    (do (assoc! ln-c :content n-str)
        (assoc! ln-c :next (line b-str ln old-nln))
        (reset! ln (persistent! ln-c))
        ln)))

(defn str-ln
  ([ln]
   (str-ln (ln-next ln) (ln-content ln)))
  ([ln acc]
   (if ln
     (recur (ln-next ln) (str acc \newline (ln-content ln)))
     acc)))
