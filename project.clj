(defproject editor-clj "0.1.0-SNAPSHOT"
  :description "A simple text editor written in Clojure"
  :dependencies [[org.clojure/clojure "1.9.0-alpha14"]]
  :main ^:skip-aot editor-clj.main
  :target-path "target/%s"
  :profiles {:dev {:source-paths ["dev"]
                   :dependencies
                   [[org.clojure/tools.namespace "0.2.10"]
                    [org.clojure/test.check "0.9.0"]]}
             :uberjar {:aot :all}})

