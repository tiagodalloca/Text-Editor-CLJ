(ns editor-clj.main
  (:require [editor-clj.core :as c])
  (:import [jline.console ConsoleReader])
  (:gen-class))

(defn -main
  "I don't do a whole lot ... yet."
  [& args]
  ;; (let [reader (ConsoleReader.)]
  ;;   (.clearScreen reader)
  ;;   (c/set-default-function! #(do (c/insert-char %)
  ;;                                 (.print reader (str %))))

  ;;   (c/map-keys! "CTRL-h" (do (c/backspace)
  ;;                             (.backspace reader))
  
  ;;                "CTRL-d" (do (c/delete)
  ;;                             (.delete reader))
  
  ;;                "CTRL-m" (do (c/breakline)
  ;;                             (.print reader (str \n)))
  
  ;;                "CTRL-f" (do (c/righthward)
  ;;                             (.moveCursor reader 1))
  
  ;;                "CTRL-b" (do (c/leftward)
  ;;                             (.moveCursor reader -1))
  
  ;;                "CTRL-n" (do (c/forward))
  
  ;;                "CTRL-p" (do (c/backward))

  ;;                "CTRL-q" (do c/quit!))
  ;;   (c/start-writing!)
  ;;   (while (not @c/quit)
  ;;     (c/resolve-keys reader)))
  (print "Nah"))
