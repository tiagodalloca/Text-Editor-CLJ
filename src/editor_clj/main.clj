(ns editor-clj.main
  (:require [editor-clj.core :as c])
  (:import [jline.console ConsoleReader])
  (:gen-class))

(defn -main
  "I don't do a whole lot ... yet."
  [& args]
  (let [reader (ConsoleReader.)]
    (.clearScreen reader)
    (c/set-default-function! c/insert-char)
    (c/map-keys! "CTRL-h" (do (c/backspace))
                 "CTRL-d" (do (c/delete))
                 "CTRL-m" (do (c/breakline))
                 "CTRL-f" (do (c/righthward))
                 "CTRL-b" (do (c/leftward))
                 "CTRL-n" (do (c/forward))
                 "CTRL-p" (do (c/backward))

                 "CTRL-q" (do c/quit!))
    (c/start-writing!)
    (while (not @c/quit)
      (c/resolve-keys reader))))
