(ns ozu.webui.handler
  (:require [ozu.webui.views :as views]
            [compojure.core :as cc]
            [compojure.handler :as handler]
            [compojure.route :as route]
            [ring.adapter.jetty :as jetty])
  (:gen-class))

(cc/defroutes app-routes
  (cc/GET "/"
       []
       (views/home-page {}))
  (cc/POST "/"
        {params :params}
        (views/home-page params))
  (route/resources "/")
  (route/not-found "Not Found"))

(def app
  (handler/site app-routes))

(defn -main
  [& [port]]
  (let [port (Integer. (or port
                           (System/getenv "PORT")
                           5000))]
    (jetty/run-jetty #'app {:port  port
                            :join? false})))
