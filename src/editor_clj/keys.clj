(ns editor-clj.keys
  (:require [editor-clj.buffer :as buffer]
            [editor-clj.window :as window]))

(defn get-chars-map
  "Use it and see the magic"
  ([& s-e]
   (let [s-e (partition 2 s-e)]
     (->> (for [[s e] s-e]
            (range (int s) (+ (int e) 1))) 
          flatten
          (map #(-> % char str))))))

(def keyboard-chars
  (set (conj (get-chars-map \a \z
                            \A \Z
                            \0 \9)
             ";" ":" "=" "+" "-" "_" "/" "?" "[" "*" "%" "$" "#" "{" "\\" "|" "]" "}" "\"" "'" "," "." "ç" "°" "º" "ª" "@" "!")))

(defn common-whatever [str-key]
  (buffer/insert-char (get str-key 0)))

(defn brk [str-key] (buffer/breakline))
(defn bsp [str-key] (buffer/backspace))
(defn c-z [str-key] (buffer/undo!))
(defn c-y [str-key] (buffer/redo!))
(defn del [str-key] (buffer/delete))
(defn quit [str-key] '(.close panel))
(defn r [str-key] (buffer/righthward))
(defn l [str-key] (buffer/leftward))
(defn u [str-key] (buffer/upward))
(defn d [str-key] (buffer/downward))

(def common-bindings
  (reduce #(assoc %1 (str "typed " %2) common-whatever) {} keyboard-chars))

(def not-so-common-bindings
  {"ENTER" brk
   "BACK_SPACE" bsp
   \space common-whatever
   "control released Z" c-z
   "control Z" c-z
   "control released Y"c-y
   "control Y" c-y
   "control D" del
   "control released D" del
   "control released Q" quit 
   "RIGHT" r
   "LEFT" l
   "UP" u
   "DOWN" d})

(def all-bindings
  (merge common-bindings not-so-common-bindings))

