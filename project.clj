(defproject foodwater-search "0.1.0"
  :description "tools for searching through recipes on foodwater.org"
  :url "http://adamtait.com/foodwater"
  :license {:name "The MIT License"
            :url "http://opensource.org/licenses/MIT"}
  :dependencies [[org.clojure/clojure "1.5.1"]
                 [compojure "1.1.6"]]
  :plugins [[lein-ring "0.8.10"]]
  :ring {:handler com.adamtait.foodwater-search.handler/app}
  :profiles
  {:dev {:dependencies [[ring-mock "0.1.5"]
                        [javax.servlet/servlet-api "2.5"]]}})
