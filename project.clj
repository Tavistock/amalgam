(defproject amalgam "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.9.0"]
                 [org.clojure/clojurescript "1.10.238"]]
  :plugins [[lein-cljsbuild "1.1.7"]
            [lein-doo "0.1.10"]
            [lein-figwheel "0.5.16"]]
  :doo {:paths {:rhino "lein run -m org.mozilla.javascript.tools.shell.Main"}}
  :aliases {"test" ["with-profile" "test" "doo" "rhino" "test" "once"]}
  :profiles
  {:test {:dependencies [[org.mozilla/rhino "1.7.7"]]
          :cljsbuild
          {:builds
           {:test
            {:source-paths ["src" "test"]
             :compiler {:output-to "target/main.js"
                        :output-dir "target"
                        :main amalgam.test-runner
                        :optimizations :simple}}}}}}
  :cljsbuild
  {:builds
   [{:id "client"
     :source-paths ["src"]
     :figwheel true
     :compiler {:parallel-build true
                :optimizations :none
                :main amalgam.core
                :output-dir "resources/public/js/out"
                :output-to  "resources/public/js/amalgam.js"
                :asset-path "js/out"
                :npm-deps false}}]})
