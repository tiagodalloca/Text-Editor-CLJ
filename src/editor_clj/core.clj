(ns editor-clj.core
  (:require [editor-clj.buffer :as buffer]
            [editor-clj.utils :as u]
            [clojure.string :as str])
  (:import [javax.swing
            JFrame
            JPanel
            JComponent
            KeyStroke
            AbstractAction
            ImageIcon]
           [java.awt.event
            ActionListener
            ComponentListener]
           [java.awt
            Graphics
            Color]))

(defrecord EditorWindow
    [frame
     panel 
     key-bindings]
  u/IWindow
  (init [this] 
    (let [p (.panel this) 
          kb (.key-bindings this)] 
      (doseq [[k f] kb
              :let [action-name (str k "-action")]]
        (doto panel
          (.. (getInputMap)
              (put (KeyStroke/getKeyStroke k) action-name))
          (.. (getActionMap)
              (put action-name
                   (proxy [AbstractAction] []
                     (actionPerformed [e]
                       (f))))))) 
      (doto frame
        (.add panel) 
        (.show))
      (.requestFocus p)))
  (print-lines [this lines]
    (let []))
  (reset [this]
    (println "Hue"))
  (quit [this]
    (println "Ahn")))

(defn hex-color [s]
  (letfn [(hexfy [i j] (Integer/valueOf (.substring s i j), 16))]
    (Color. (hexfy 1 3) (hexfy 3 5) (hexfy 5 7))))

(defn paint-editor [g w h] 
  (.setColor g (hex-color "#25333c"))
  (.fillRect g 0 0 w h))

(def editor-panel
  `(proxy [JPanel] []
     (paintComponent [g]
       (proxy-super paintComponent g)
       (let [size (.getSize this)
             w (.width size)
             h (.height size)]
         (paint-editor g w h)))))

(defn editor-window
  ([[w h :as dimensions] key-bindings icon-path]
   (let [frame (JFrame.)
         panel editor-panel]
     (doto frame
       (.setSize w h)
       (.setIconImage (ImageIcon. icon-path)))
     (doto panel
       (.setSize w h))
     (->EditorWindow frame panel key-bindings)))
  ([[w h :as dimensions] key-bindings]
   (let [frame (JFrame.)
         panel editor-panel] 
     (doto frame
       (.setSize w h))
     (->EditorWindow frame panel key-bindings))))

