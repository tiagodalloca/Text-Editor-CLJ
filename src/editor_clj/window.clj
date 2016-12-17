(ns editor-clj.window
  (:require [editor-clj.buffer :as buffer]
            [editor-clj.utils :as u]
            [editor-clj.doubly-linked-list :refer :all]
            [clojure.string :as str]
            [clojure.java.io :as io])
  (:import [java.io Writer FileWriter IOException]
           [javax.swing JFrame JPanel JComponent JFileChooser KeyStroke AbstractAction ImageIcon]
           [java.awt.event ActionListener ActionEvent ComponentListener]
           [java.awt Graphics Font Color] 
           [java.nio.charset Charset]))

(defn config-frame [frame [w h] panel]
  (doto frame
    (.setSize w h) 
    (.add panel)))

(defn config-bindings [component key-bindings]
  (doseq [[k f] key-bindings
          :let [action-name (str k)]]
    (doto component
      (.. (getInputMap)
          (put (KeyStroke/getKeyStroke k) action-name))
      (.. (getActionMap)
          (put action-name
               (proxy [AbstractAction] []
                 (actionPerformed [e] 
                   (f (.getActionCommand e))
                   (.repaint component))))))))

(defn hex-color [s]
  (letfn [(hexfy [i j] (Integer/valueOf (.substring s i j), 16))]
    (Color. (hexfy 1 3) (hexfy 3 5) (hexfy 5 7))))

(defn paint-editor [g w h lines [x y]]
  (doto g
    (.setFont (Font. "Monospaced" Font/PLAIN 15))
    (.setColor (hex-color "#25333c"))
    (.fillRect 0 0 w h)
    (.setColor (hex-color "#FFFFFF")))
  (let [u-border 30
        r-border 10
        lw (.. g (getFontMetrics) (charWidth \space))
        lh (.. g (getFontMetrics) (getHeight))
        w (- w lw)
        nrows (int (/ h lh))
        ncols (int (/ w lw))]
    (loop [seq-lines (u/slice-seq (u/lines-as-seq lines) (- (inc y) nrows) y)
           y u-border] 
      (if (not-empty seq-lines)
        (do (.drawString g (if (>= (inc x) ncols)
                             (let [l (first seq-lines)
                                   ll (.length l)
                                   x (inc x)]
                               (.substring l
                                           (- x ncols)
                                           (if (> x ll)
                                             ll x)))
                             (first seq-lines))
                         r-border y)
            (recur (drop 1 seq-lines) (+ y lh))))) 
    (doto g
      (.setColor Color/ORANGE)
      (.setFont (Font. "Monospaced" Font/PLAIN 20)) 
      (.drawString "|" 
                   (let [cx (int (+ r-border (* (dec x) lw) (/ lw 2)))]
                     (if (<= cx w) cx (int (- w (/ lw 2)))))
                   (let [cy (+ u-border (* y lh))] 
                     (if (< cy h) cy (int (- h (* 1/5 lh)))))))))

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
  ([[w h :as dimensions] editor-state icon-path]
   (let [frame (JFrame.)
         panel (editor-panel editor-state)]
     (config-frame frame [w h] panel)     
     (.setIconImage frame (.getImage (ImageIcon. icon-path))) 
     (->EditorWindow frame panel)))
  ([[w h :as dimensions] editor-state]
   (let [frame (JFrame.)
         panel (editor-panel editor-state)] 
     (config-frame frame [w h] panel) 
     (->EditorWindow frame panel))))

(defn config-window-bindings
  [{:keys [frame panel] :as e-window} key-bindings]
  (config-bindings panel key-bindings))

(defn init-editor-window
  [{:keys [frame panel] :as ew}]
  (doto frame (.show))
  (doto panel (.repaint) (.requestFocus)))

(defn save-file
  [parent lines]
  (let [jfc (JFileChooser.)]
    (when (= (.showSaveDialog jfc parent) JFileChooser/APPROVE_OPTION)
      (try (with-open [w (io/writer (.. jfc getSelectedFile getPath)
                                    :encoding "UTF-8")] 
             (doseq [line (u/lines-as-seq lines)] 
               (doto w (.write line) (.newLine))))
           (catch Exception e (println e))))))

(defn open-file
  [parent lines]
  (let [jfc (JFileChooser.)]
    (when (= (.showOpenDialog jfc parent) JFileChooser/APPROVE_OPTION)
      (try (with-open [i (io/reader (.. jfc getSelectedFile getPath)
                                    :encoding "UTF-8")] 
             (buffer/set-editor! :lines (-> i line-seq reverse dbl)))
           (catch Exception e (println e))))))


(defn make-fn-save [{:keys [frame panel] :as ew}]
  (fn [str-key] (save-file panel (:lines @buffer/editor-state))))
(defn make-fn-open [{:keys [frame panel] :as ew}]
  (fn [str-key]
    (open-file panel (:lines @buffer/editor-state)) (.repaint panel)))

