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

(defn dbl
  "Constructs a doubly-linked list, where s is a sequence to start off"
  [s]
  (let [s (seq s)]
    {:next (pop s)
     :prev '()
     :curr (peek s)}))

(defn dbl-next 
  [{:keys [next prev curr] :as l}]
  (when-not (empty? next)    
    {:next (pop next)
     :prev (conj prev curr)
     :curr (peek next)}))

(defn dbl-prev
  [{:keys [next prev curr] :as l}]
  (when-not (empty? prev)
    {:next (conj next curr)
     :prev (pop prev)
     :curr (peek prev)}))

(defn dbl-add-after
  [l x]
  (update l :next #(conj % x)))

(defn dbl-add-before
  [l x]
  (update l :prev #(conj % x)))

(defn ln-insert-char
  "Inserts c into the line :content at the index i"
  [ln c i] 
  (ln update :content #(str-insert % i c)))

(defn ln-replace-char
  "Replaces a char at the index i of line for c"
  [ln c i]
  (update ln :content #(str-replace % i c)))

(defn ln-delete-char
  "Deletes the char of the i index"
  [ln i]
  (update ln :content #(str-delete % i)))

(defn merge-lines
  "Concats the ln's :content with oln's and sets ln's :next to 
  oln's"
  ([ln oln] 
   (let [tln (transient ln)
         oln oln
         s1 (:content tln)
         s2 (:content oln)
         st (str s1 s2)]   
     (do (assoc! tln :content st)
         (assoc! tln :next (:next oln))
         (persistent! tln)))))

;; (defn breakline-at
;;   "Breaks the line at index i and updates :next"
;;   [ln i]
;;   (let [n-str (subs (ln-content ln) 0 i)
;;         b-str (subs (ln-content ln) i)
;;         old-nln (ln-next ln)

;;         ln-c (transient @ln)]
;;     (do (assoc! ln-c :content n-str)
;;         (assoc! ln-c :next (line b-str ln old-nln))
;;         (reset! ln (persistent! ln-c))
;;         ln)))

;; (defn str-ln
;;   ([ln]
;;    (str-ln (ln-next ln) (ln-content ln)))
;;   ([ln acc]
;;    (if ln
;;      (recur (ln-next ln) (str acc \newline (ln-content ln)))
;;      acc)))
