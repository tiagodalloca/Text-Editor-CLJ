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
            ActionEvent
            ComponentListener]
           [java.awt
            Graphics
            Font
            Color]))

(defn config-frame [frame w h panel]
  (doto frame
    (.setSize w h) 
    (.add panel)))

(defn config-pane [panel w h key-bindings]
  (doseq [[k f] key-bindings
          :let [action-name (str k)]]
    (doto panel
      (.. (getInputMap)
          (put (KeyStroke/getKeyStroke k) action-name))
      (.. (getActionMap)
          (put action-name
               (proxy [AbstractAction] []
                 (actionPerformed [e] 
                   (f (.getActionCommand e))
                   (.repaint panel))))))))

(defn hex-color [s]
  (letfn [(hexfy [i j] (Integer/valueOf (.substring s i j), 16))]
    (Color. (hexfy 1 3) (hexfy 3 5) (hexfy 5 7))))

(defn paint-editor [g w h lines [x y]]
  (doto g
    (.setFont (Font. "Monospaced" Font/PLAIN 20))
    (.setColor (hex-color "#25333c"))
    (.fillRect 0 0 w h)
    (.setColor (hex-color "#FFFFFF")))
  (let [u-border 30
        l-border 30
        lw (.. g (getFontMetrics) (charWidth \space))
        lh (.. g (getFontMetrics) (getHeight))]
    (loop [seq-lines (u/lines-as-seq lines)
           y u-border]
      (if (not-empty seq-lines)
        (do (.drawString g (first seq-lines) l-border y)
            (recur (drop 1 seq-lines) (+ y lh)))))
    (doto g
      (.setColor Color/ORANGE)
      (.setFont (Font. "Monospaced" Font/PLAIN 23)) 
      (.drawString "|"
                   (+ l-border (* (dec x) lw) (/ lw 2))
                   (+ u-border (* y lh))))))

(defmacro editor-panel
  [buffer-atom]
  `(proxy [JPanel] []
     (~'paintComponent [g#]
      (proxy-super ~'paintComponent g#)
      (let [size# (.getSize ~'this)
            w# (.width size#)
            h# (.height size#)
            b# @~buffer-atom]
        (paint-editor g# w# h# (:lines b#) [(:curr-coln b#)
                                            (:curr-row b#)])))))

(defrecord EditorWindow
    [frame panel])

(defn editor-window
  ([[w h :as dimensions] editor-state key-bindings icon-path]
   (let [frame (JFrame.)
         panel (editor-panel editor-state)]
     
     (doto frame
       (config-frame panel w h)
       (.setIconImage (ImageIcon. icon-path)))
     (config-pane panel w h key-bindings)
     (->EditorWindow frame panel)))
  ([[w h :as dimensions] editor-state key-bindings]
   (let [frame (JFrame.)
         panel (editor-panel editor-state)] 
     (config-pane panel w h key-bindings)
     (config-frame frame w h panel) 
     (->EditorWindow frame panel))))

(defn init-editor-window
  [{:keys [frame panel] :as editor-window}]
  (doto frame
    (.show))
  (doto panel
    (.repaint)
    (.requestFocus)))

