(defproject ozu "0.1"
  :description "Spanish town name generator using Markov chanis"
  :url "http://example.com/FIXME"
  :min-lein-version "2.0.0"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.8.0"]
                 [compojure "1.4.0"]
                 [ring/ring-defaults "0.1.5"]
                 [ring/ring-jetty-adapter "1.5.0"]
                 [hiccup "1.0.2"]]
  :plugins [[lein-ring "0.9.7"]]
  :ring {:handler ozu.webui.handler/app}
  :main ozu.webui.handler
  :profiles
  {:dev {:dependencies [[javax.servlet/servlet-api "2.5"]
                        [ring/ring-mock "0.3.0"]]}})
