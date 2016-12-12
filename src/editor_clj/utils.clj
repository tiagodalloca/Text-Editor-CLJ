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

(defrecord DoubleLL
    [next prev curr]
  Object
  (toString [{:keys [next prev curr] :as l}]
    (str "("
         (reduce #(str %1 " " %2) "" (reverse prev))
         " [ " curr " ] "
         (reduce #(str %1 " " %2) "" next)
         ")")))

(defmethod clojure.core/print-method DoubleLL [x writer]
  (.write writer (str x)))

(defn dbl
  "Constructs a doubly-linked list, where s is a sequence to start off"
  [s]
  (let [s (seq s)]
    (->DoubleLL (pop s)
                '()
                (peek s))))

(defn dbl-next
  "Updates :curr to :next's peek, pushes :curr to :prev and pops :next"
  [{:keys [next prev curr] :as l}]
  (when-not (empty? next) 
    (-> l
        (update :next pop)
        (update :prev #(conj % curr))
        (assoc :curr (peek next)))))

(defn dbl-prev
  "Updates :curr to :prev's peek, pushes :curr to :next and pops :prev"
  [{:keys [next prev curr] :as l}]
  (when-not (empty? prev) 
    (-> l
        (update :next #(conj % curr))
        (update :prev pop)
        (assoc :curr (peek prev)))))

(defn dbl-delete
  "Deletes the current element"
  [{:keys [next prev curr] :as l}]  
  (-> l
      (update :next pop) 
      (assoc :curr (peek next))))

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
    (-> (update l :next pop) 
        (assoc :curr nstr))))

(defn breakline-at
  "Breaks :curr at index i, updates :next and :curr. l is a doubly-linked 
  list"
  [{:keys [next prev curr] :as l} i]
  (-> l
      (update :prev #(conj % (.substring curr 0 i)))
      (update :curr #(.substring % i))))

(defprotocol IWindow
  "Abstraction of a window in which can be written lines, positioned
  a cursor and mapped keys"
  (init [this]
    "Do anything needed to work") 
  (print-lines [this lines]
    "Print lines, where \"lines\" is a seq of string") 
  (reset [this]
    "Clears everything")
  (quit [this]))

