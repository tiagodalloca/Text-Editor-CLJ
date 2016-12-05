(ns editor-clj.core
  (:require [editor-clj.structures :refer :all]))

(def curr-ln (atom nil))
(def curr-coln (atom 0))

(defn start-writing
  "Configurates the global vars to write"
  []
  (do (reset! curr-ln (line "" nil nil))
      (reset! curr-coln 0)))

(defn breakline
  "Breaks the curr(ent) line in the curr(ent) coln"
  []
  (let [ln @curr-ln]
    (breakline-at ln @curr-coln)
    (reset! curr-ln (ln-next ln))))

(defn insert-char
  "Inserts a char at the curr(ent) position"
  [c]
  (ln-insert-char @curr-ln c))

(defn replace-char
  "Replaces a char at the curr(ent) position by a give char c"
  [c]
  (ln-replace-char @curr-ln c @curr-coln))

(defn backspace
  "Does exactly what you'd expect backspace to do"
  []
  (let [coln @curr-coln]
    (when (> coln 0) ;; > 0
      (ln-delete-char @curr-ln (dec coln)))))

(defn delete
  "Deletes the char at the curr(ent) position"
  []
  (ln-delete-char @curr-ln @curr-coln)) ;; needs some verifications

(defn forward
  "Moves backward"
  [] 
  (let [nln (ln-next @curr-ln)
        coln @curr-coln]
    (when nln
      (do (reset! curr-ln nln)
          (reset! curr-coln
                  (let [nln-s (.length nln)]
                    (if (< coln nln-s)
                      coln nln-s)))))))

(defn backward
  "Moves backward"
  [] 
  (let [pln (ln-prev @curr-ln)
        coln @curr-coln]
    (when pln
      (do (reset! curr-ln pln)
          (reset! curr-coln
                  (let [pln-s (.length pln)]
                    (if (< coln pln-s)
                      coln pln-s)))))))

(defn leftward
  "Moves leftward"
  []
  (let [coln @curr-coln
        ln   @curr-ln
        ln-s (.length (ln-content ln))]
    (if (< coln ln-s)
      (swap! curr-coln inc)
      (when-let [nln (ln-next ln)]
        (do (reset! curr-ln nln)
            (reset! curr-coln 0))))))

(defn righthward
  "Moves righthward"
  []
  (let [coln @curr-coln
        ln   @curr-ln]
    (if (> coln 0)
      (swap! curr-coln dec)
      (when-let [pln (ln-prev ln)]
        (do (reset! curr-ln pln)
            (reset! curr-coln
                    (-> (ln-content pln)
                        (.length)
                        (dec))))))))


