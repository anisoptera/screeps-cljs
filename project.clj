(defproject cljs-screeps "0.1.0-SNAPSHOT"
  :description "screeps clojurescript"
  :url "http://example.com/FIXME"
  :dependencies [[org.clojure/clojure "1.7.0"]
                 [org.clojure/clojurescript "1.7.48" :classifier "aot"
                  :exclusion [org.clojure/data.json]]
                 [cheshire "5.5.0"]
                 [org.clojure/data.json "0.2.6" :classifier "aot"]
                 [com.cognitect/transit-cljs "0.8.239"]
                 [clj-http "2.0.0"]
                 [org.clojure/data.codec "0.1.0"]]
  :jvm-opts ^:replace ["-Xmx1g" "-server"]
  :plugins [[lein-npm "0.6.1"]]
  :npm {:dependencies [[source-map-support "0.3.2"]]}
  :source-paths ["src" "target/classes"]
  :clean-targets ["out" "release"]
  :target-path "target")
