(ns editor-clj.core)

(defn str-insert
  "Insert c in string s at index i."
  [s c i]
  (str (subs s 0 i) c (subs s i)))

(defn str-replace
  "Replaces a char at the index i of s for c"
  [s c i]
  (str (subs s 0 i) c (subs s (inc i) (.length s))))

(defn line
  "Returns a map representing a line"
  [s previous-line next-line]
  {:content s 
   :next next-line
   :prev previous-line})
;; Think I'll use refs

(defn next
  [ln]
  @(:next ln))

(defn prev
  [ln]
  @(:prev ln))

(defn ln-insert-char
  "Inserts c into the line :content at the index i"
  [ln c i] 
  (update line :content #(str-insert % i c)))

(defn ln-replace-char
  "Replaces a char at the index i of line for c"
  [ln c i]
  (update line :content #(str-replace % i c)))

(defn append-line
  "Updates :next field from line"
  [line nline]
  (update line :next nline))

(defn append-line-before
  "Updates :prev field from line"
  [ln nln]
  (update line :prev nline))

(defn breakline-at
  "Breaks the line at index i and updates :next"
  [ln i]
  (let [n-str (subs (:content ln) 0 i)
        b-str (subs (:content ln) i)
        old-next-line (:next ln)

        ln (transient ln)]
    (do (assoc! ln :content n-str)
        (let [ln (persistent! ln)]
          (assoc ln :next (line b-str ln old-next-line))))))
