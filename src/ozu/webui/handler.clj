(ns ozu.webui.handler
  (:require [ozu.webui.views :as views]
            [compojure.core :as cc]
            [compojure.handler :as handler]
            [compojure.route :as route]
            [ring.util.response :refer [redirect]]
            [ring.adapter.jetty :as jetty])
  (:gen-class))

(cc/defroutes app-routes
  (cc/GET "/"  {params :params}
       (views/home-page params))
  (cc/POST "/" {params :params}
        (redirect (str "/?ca-cod=" (:ca-cod params) "&n=" (:n params)) :see-other))
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
