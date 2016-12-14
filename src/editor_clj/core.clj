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

(defn config-frame [frame w h panel]
  (doto frame
    (.setSize w h) 
    (.add panel)))

(defn config-panel [panel w h key-bindings]
  (doseq [[k f] key-bindings
          :let [action-name (str k "-action")]]
    (doto panel
      (.. (getInputMap)
          (put (KeyStroke/getKeyStroke k) action-name))
      (.. (getActionMap)
          (put action-name
               (proxy [AbstractAction] []
                 (actionPerformed [e]
                   (f))))))))

(defn hex-color [s]
  (letfn [(hexfy [i j] (Integer/valueOf (.substring s i j), 16))]
    (Color. (hexfy 1 3) (hexfy 3 5) (hexfy 5 7))))

(defn paint-editor [g w h lines]
  (doto g
    (.setColor (hex-color "#25333c"))
    (.fillRect 0 0 w h)
    (.setColor (hex-color "#FFFFFF"))
    (.drawString (:curr lines) (int (/ w 2)) (int (/ h 2)))))

(defmacro editor-panel
  [buffer-atom]
  `(proxy [JPanel] []
     (~'paintComponent [g#]
      (proxy-super ~'paintComponent g#)
      (let [size# (.getSize ~'this)
            w# (.width size#)
            h# (.height size#)]
        (paint-editor g# w# h# (:lines @~buffer-atom))))))

(defrecord EditorWindow
    [frame panel])

(defn editor-window
  ([[w h :as dimensions] editor-state key-bindings icon-path]
   (let [frame (JFrame.)
         panel (editor-panel editor-state)]
     
     (doto frame
       (config-frame panel w h)
       (.setIconImage (ImageIcon. icon-path)))
     (config-panel panel w h key-bindings)
     (->EditorWindow frame panel)))
  ([[w h :as dimensions] editor-state key-bindings]
   (let [frame (JFrame.)
         panel (editor-panel editor-state)] 
     (config-panel panel w h key-bindings)
     (config-frame frame w h panel) 
     (->EditorWindow frame panel))))

(defn init-editor-window
  [{:keys [frame panel] :as editor-window}]
  (doto frame
    (.show))
  (doto panel
    (.repaint)
    (.requestFocus)))

