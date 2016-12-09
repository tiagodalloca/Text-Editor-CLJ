(ns editor-clj.core
  (:require [editor-clj.structures :refer :all]
            [editor-clj.key-resolver :as kr])
  (:import [jline.console ConsoleReader]
           [java.util Stack]))

(def editor-state
  (atom {:curr-ln nil
         :curr-coln nil
         :insert-mode nil
         :keys-map {:default-function #(do nil)}}))

(def action-stack (Stack.))
(def state-stack (Stack.))

(add-watch editor-state :add-stack
           (fn [k r o n]
             (.push state-stack o)))

(defn can-undo?
  []
  (not (or (.isEmpty action-stack)
           (.isEmpty state-stack))))

(defn undo!
  "Pop the action and the state stack"
  []
  (when (can-undo?)
    (let [old-s (.pop state-stack)
          f (.pop action-stack)])))

(defn get-editor
  [& k]
  (get-in @editor-state k))

(defn update-editor!
  "Updates the editor state, where f is a function that will do the magic
  and k is a keyword that indicates what will be updated"
  ([k f]
   (swap! editor-state update k f))
  ([k f & args]
   (swap! editor-state update k f args)))

(defn set-editor!
  "No docsting provided"
  [k v]
  (swap! editor-state assoc k v))

(defn set-default-function!
  [f]
  (update-editor! :keys-map
                  #(assoc % :default-function f)))

(defmacro map-keys!
  [& binding-form]
  `(let [b# (kr/map-bindings ~@binding-form)] 
     (update-editor!
      :keys-map
      #(merge % b#))))

(defn start-writing
  "Configurates the global vars to write"
  []
  (set-editor! :curr-ln (line "" nil nil))
  (set-editor! :curr-coln 0)
  (set-editor! :insert-mode true))

(defn toggle-insert
  "Toggles insert-mode"
  []
  (update-editor! :insert-mode not))

(defn breakline
  "Breaks the curr(ent) line in the curr(ent) coln"
  []
  (let [ln (get-editor :curr-ln)]
    (breakline-at ln (get-editor :curr-coln))
    (set-editor! :curr-ln (ln-next ln))))

(defn insert-char
  "Inserts a char at the curr(ent) position"
  [c]
  (let [ln (get-editor :curr-ln)
        coln (get-editor :curr-coln)
        ln-s (.length (ln-content ln))]
    (do ((if (and (not (toggle-insert))
                  (< coln ln-s))
           ln-replace-char
           ln-insert-char)
         ln coln c)
        (update-editor! :curr-coln inc))))

(defn backspace
  "Does exactly what you'd expect backspace to do"
  []
  (let [coln (get-editor :curr-coln)]
    (if (> coln 0)
      (ln-delete-char (get-editor :curr-ln) (dec coln))
      (let [ln (get-editor :curr-ln)
            pln (ln-prev ln)]
        (when pln
          (merge-lines pln ln))))))

(defn delete
  "Deletes the char at the curr(ent) position"
  []
  (let [coln (get-editor :curr-coln)
        ln (get-editor :curr-ln)] 
    (if (and (> coln 0)
             (> (.length ln) 0))
      (ln-delete-char ln coln)
      (when (ln-next ln)
        (merge-lines ln)))))

(defn forward
  "Moves forward"
  [] 
  (let [nln (ln-next (get-editor :curr-ln))
        coln (get-editor :curr-coln)]
    (when nln
      (do (set-editor! :curr-ln nln)
          (set-editor! :curr-coln
                       (let [nln-s (.length nln)]
                         (if (< coln nln-s)
                           coln nln-s)))))))

(defn backward
  "Moves backward"
  [] 
  (let [pln (ln-prev (get-editor :curr-ln))
        coln (get-editor :curr-coln)]
    (when pln
      (do (set-editor! :curr-ln pln)
          (set-editor! :curr-coln
                       (let [pln-s (.length pln)]
                         (if (< coln pln-s)
                           coln pln-s)))))))

(defn righthward
  "Moves righthward"
  []
  (let [coln (get-editor :curr-coln)
        ln   (get-editor :curr-ln)
        ln-s (.length (ln-content ln))]
    (if (< coln ln-s)
      (update-editor! :curr-coln inc)
      (when-let [nln (ln-next ln)]
        (do (set-editor! :curr-ln nln)
            (set-editor! :curr-coln 0))))))

(defn leftward
  "Moves leftward"
  []
  (let [coln (get-editor :curr-coln)
        ln   (get-editor :curr-ln)]
    (if (> coln 0)
      (update-editor! :curr-coln dec)
      (when-let [pln (ln-prev ln)]
        (do (set-editor! :curr-ln pln)
            (set-editor! :curr-coln
                         (-> (ln-content pln)
                             (.length)
                             (dec))))))))

(defn resolve-keys
  "Wrapper function for resolve-keystroke, from key_resolver"
  [^ConsoleReader reader]
  (kr/resolve-keystroke (get-editor :keys-map) reader))
