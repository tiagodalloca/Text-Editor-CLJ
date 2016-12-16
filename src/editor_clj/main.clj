(ns editor-clj.main
  (:require [editor-clj.window :as w]
            [editor-clj.keys :as k]
            [editor-clj.buffer :as b])
  (:gen-class))

(defn -main
  "I don't do a whole lot ... yet."
  [& args]
  (let [window (w/editor-window [800 600] b/editor-state
                                (.getPath
                                 (clojure.java.io/resource "icon-1.png")))]
    (b/start-writing!)
    (w/config-window-bindings window (assoc k/all-bindings
                                            "control released S"
                                            (w/make-fn-save window)
                                            "control released O"
                                            (w/make-fn-open window)))
    (w/init-editor-window window)))
