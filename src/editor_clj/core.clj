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
          im (.getInputMap sp)
          kb (.key-bindings this)] 
      (doseq [[k f] kb] 
        (doto sp
          (.. (getInputMap)
              (.put (KeyStroke/getKeyStroke k) "hue"))
          (.. (getActionMap)
              (.put "hue"
                    (proxy [AbstractAction] []
                      (actionPerformed [e]
                        (println "Action performed!"))))))
        (doto sf
          (.add sp)
          (.show)))))
  (print-lines [this lines]
    (println "Duh"))
  (reset [this]
    (println "Hue"))
  (quit [this]
    (println "Ahn")))

;; (def hue (EditorWindow. (JFrame.) (JPanel.) [0 0] {"control s" #(println "I don't like u")}))

;; (def hh (JPanel.))

;; (def huhue (doto (JFrame.)
;;              (.add hh)
;;              (.show)))

;; (.requestFocus hh)

;; (.put (.getInputMap hh) (KeyStroke/getKeyStroke "s")
;;       (proxy [AbstractAction] []
;;         (actionPerformed [e]
;;           (println "Action performed!"))))

;; (.init hue)
