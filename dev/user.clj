(ns user
  (:require [editor-clj.main :as m]
            [editor-clj.buffer :as b]
            [editor-clj.window :as w] 
            [editor-clj.utils :as u]
            [editor-clj.keys :as k]
            [editor-clj.doubly-linked-list :refer :all]
            [clojure.java.io :as io]
            [clojure.string :as str]
            [clojure.pprint :refer (pprint)]
            [clojure.repl :as repl]
            [clojure.test :as test]
            [clojure.tools.namespace.repl :refer (refresh refresh-all)]

            [clojure.tools.nrepl.server :as nrepl-server]
            [cider.nrepl :refer (cider-nrepl-handler)])
  (:gen-class))

(defn -main [& args]
  (println "Starting server...")
  (nrepl-server/start-server :port 7888 :handler cider-nrepl-handler)
  (println "Server started at port 7888"))

