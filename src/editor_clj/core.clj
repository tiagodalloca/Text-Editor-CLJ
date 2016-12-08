(ns editor-clj.core
  (:require [editor-clj.structures :refer :all]
            [editor-clj.key-resolver :as kr])
  (:import [jline.console ConsoleReader]))

(def curr-ln (atom nil))
(def curr-coln (atom nil))
(def insert-mode (atom nil))

(def keys-map (atom {:default-function #(do nil)}))

(defn set-default-function!
  [f]
  (swap! keys-map assoc :default-function f))

(defmacro map-keys!
  [& binding-form]
  `(->> (kr/map-bindings ~@binding-form)
        (apply swap! keys-map merge)))

(defn start-writing
  "Configurates the global vars to write"
  []
  (reset! curr-ln (line "" nil nil))
  (reset! curr-coln 0)
  (reset! insert-mode true))

(defn toggle-insert
  "Toggles insert-mode"
  []
  (swap! insert-mode not))

(defn breakline
  "Breaks the curr(ent) line in the curr(ent) coln"
  []
  (let [ln @curr-ln]
    (breakline-at ln @curr-coln)
    (reset! curr-ln (ln-next ln))))

(defn insert-char
  "Inserts a char at the curr(ent) position"
  [c]
  (let [ln @curr-ln
        coln @curr-coln
        ln-s (.length (ln-content ln))]
    (do ((if (and (not (toggle-insert))
                  (< coln ln-s))
           ln-replace-char
           ln-insert-char)
         ln @curr-coln c)
        (swap! curr-coln inc))))

(defn backspace
  "Does exactly what you'd expect backspace to do"
  []
  (let [coln @curr-coln]
    (if (> coln 0)
      (ln-delete-char @curr-ln (dec coln))
      (let [ln @curr-ln
            pln (ln-prev ln)]
        (when pln
          (merge-lines pln ln))))))

(defn delete
  "Deletes the char at the curr(ent) position"
  []
  (let [coln @curr-coln
        ln @curr-ln] 
    (if (and (> coln 0)
             (> (.length ln) 0))
      (ln-delete-char ln coln)
      (when (ln-next ln)
        (merge-lines ln)))))

(defn forward
  "Moves forward"
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

(defn righthward
  "Moves righthward"
  []
  (let [coln @curr-coln
        ln   @curr-ln
        ln-s (.length (ln-content ln))]
    (if (< coln ln-s)
      (swap! curr-coln inc)
      (when-let [nln (ln-next ln)]
        (do (reset! curr-ln nln)
            (reset! curr-coln 0))))))

(defn leftward
  "Moves leftward"
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

(defn resolve-keys
  "Wrapper function for resolve-keystroke, from key_resolver"
  [^ConsoleReader reader]
  (kr/resolve-keystroke keys-map reader ))
