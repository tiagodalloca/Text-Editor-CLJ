(ns editor-clj.core
  (:require [editor-clj.buffer :as buffer]
            [editor-clj.utils :as u]
            [clojure.string :as str])
  (:import [javax.swing
            JFrame JPanel KeyStroke AbstractAction]
           [java.awt.event ActionListener]))

(defrecord EditorWindow
    [s-frame
     s-panel
     cursor
     key-bindings]
  u/IWindow
  (init [this] 
    (let [sp (.s-panel this)
          sf (.s-frame this) 
          kb (.key-bindings this)] 
      (doseq [[k f] kb
              :let [action-name (str k "-action")]]
        (doto sp
          (.. (getInputMap)
              (put (KeyStroke/getKeyStroke k) action-name))
          (.. (getActionMap)
              (put action-name
                   (proxy [AbstractAction] []
                     (actionPerformed [e]
                       (f)))))))
      (doto sf
        (.add sp)
        (.show))
      (.requestFocus sp)))
  (print-lines [this lines]
    (println "Duh"))
  (reset [this]
    (println "Hue"))
  (quit [this]
    (println "Ahn")))

;; (def hue (EditorWindow. (JFrame.) (JPanel.) [0 0]
;;                         {"typed s" #(println "typed s")
;;                          "control S" #(println "alt s")}))

;; (.init hue)
