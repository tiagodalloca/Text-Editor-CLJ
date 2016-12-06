(ns editor-clj.key-resolver
  (:require [clojure.string :as str])
  (:import [jline.console ConsoleReader]))

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

(defn create-binding
  "Creates a keybinding that can be resolved later from a string binding
  and a fn that will be evaluated later"
  [binding fn]
  (->> (str/split binding #" ")
       (map #(get avaiable-keys %))))


(defn resolve-keystroke
  "Resolves a keystroke based on a hash-map of bindings"
  [hash reader]
  (flush)
  ;; (loop [code (.readCharacter reader)
  ;; candidates]
  ;; )
  )
