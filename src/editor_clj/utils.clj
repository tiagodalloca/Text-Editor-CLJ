(ns editor-clj.utils
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
  "Updates :curr to :next's peek, pushes :curr to :prev and pops :next"
  [{:keys [next prev curr] :as l}]
  (when-not (empty? next)    
    {:next (pop next)
     :prev (conj prev curr)
     :curr (peek next)}))

(defn dbl-prev
  "Updates :curr to :prev's peek, pushes :curr to :next and pops :prev"
  [{:keys [next prev curr] :as l}]
  (when-not (empty? prev)
    {:next (conj next curr)
     :prev (pop prev)
     :curr (peek prev)}))

(defn dbl-add-after
  "Adds x to l's :next"
  [l x]
  (update l :next #(conj % x)))

(defn dbl-add-before
  "Adds x to l's :prev"
  [l x]
  (update l :prev #(conj % x)))

(defn dbl-get-next
  "Gets the next element of l"
  [l]
  (first (:next l)))

(defn dbl-get-prev
  "Gets the previous element l"
  [l]
  (first (:prev l)))

(defn dbl-delete
  "Deletes the current element"
  [{:keys [next prev curr] :as l}]
  {:next (pop next)
   :prev prev
   :curr (peek next)})

(defn dbl-delete-next
  "Deletes the current element"
  [l]
  (update l :next pop))

(defn dbl-delete-prev
  "Deletes the current element"
  [l]
  (update :prev pop))

(defn merge-lines
  "l is supposed to be a list of strings and the next line will be
  concatenated with the current one." 
  [{:keys [next prev curr] :as l}]
  (let [nstr (str curr (peek next))]
    {:next (pop next)
     :prev prev
     :curr nstr}))

(defn breakline-at
  "Breaks :curr at index i, updates :next and :curr. l is a doubly-linked 
  list"
  [{:keys [next prev curr] :as l} i]
  {:next next 
   :prev (conj prev (.substring curr 0 i))
   :curr (.substring curr i)})

