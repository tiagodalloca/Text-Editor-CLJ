(ns editor-clj.key-resolver
  (:require [editor-clj.utils :as u]
            [clojure.string :as str]))

(defn get-chars-map
  "Use it and see the magic"
  ([& s-e]
   (let [s-e (partition 2 s-e)]
     (->> (for [[s e] s-e]
            (range (int s) (+ (int e) 1))) 
          flatten
          (reduce (fn [acc v]
                    (assoc acc (-> v char str) v))
                  {})))))

(def ctrl-keys
  (->> (range (int \) (+ (int \) 1)) 
       (reduce (fn [acc v]
                 (assoc acc (->> v (+ 96) char (str "CTRL-")) v))
               {})))


(def avaiable-keys
  (-> (get-chars-map \a \z
                     \A \Z
                     \0 \9)
      (into ctrl-keys)))

(defn convert-keys
  "Converts the string key based on avaiable-keys"
  [keystrokes]
  (for [key (str/split keystrokes #" ")]
    (get avaiable-keys key)))

(defn create-binding
  "Creates a vector representing a keybinding which can be resolved later.
  It takes a string binding and a impure f(unction)."
  [binding f]
  [(convert-keys binding) f])


(defn append-binding
  "Appends a binding to a hash-map of bindings"
  [hash binding f]
  (let [new-binding (create-binding binding f)
        [k v] new-binding]
    (assoc hash k v)))

(defn- append-quoted-binding
  [hash binding f]
  (let [new-binding (create-binding binding f)
        [k v] new-binding]
    (assoc hash `'~k v)))

(defmacro form-func
  "Transforms a form to a function"
  [form]
  `(fn [] ~form))

(defmacro map-bindings
  [& binding-form]
  (let [binding-form (partition 2 binding-form)]
    (reduce (fn [acc [binding form]] 
              (append-quoted-binding acc binding
                                     `(form-func ~form)))
            {} binding-form)))

(defn find-keystroke
  "Finds (but doesn't execute) a binding based on a hash-map of bindings
  and a sequence of keystrokes (seq of key codes)"
  [hash keys] 
  (let [hash (dissoc hash :default-function)
        ret (loop [code (first keys)
                   keys (drop 1 keys)
                   candidates (find-candidates hash code)]
              (if (or (> (count candidates) 1)
                      (> (count keys) 1)) 
                (let [code (first keys)
                      keys (drop 1 keys)
                      candidates (find-candidates candidates code)]
                  (recur code keys candidates))
                candidates))]
    (when (not (empty? ret))
      (first (vals ret)))))

