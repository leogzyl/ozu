(ns ozu.webui.handler
  (:require [ozu.webui.views :as views]
            [compojure.core :as cc]
            [compojure.handler :as handler]
            [compojure.route :as route]
            [ring.util.response :refer [redirect]]
            [ring.adapter.jetty :as jetty])
  (:gen-class))

(defn gen-error
  [status message]
  {:status status
   :headers {"Content-Type" "text/html; charset=utf-8"}
   :body (views/error-page message)})

(cc/defroutes app-routes
  (cc/GET "/"  {params :params}
       (views/home-page params))
  (cc/POST "/" {params :params}
        (redirect (str "/?ca-cod=" (:ca-cod params) "&n=" (:n params)) :see-other))
  (route/resources "/")
  (route/not-found (gen-error 404 "El recurso solicitado no se ha encontrado")))

(defn wrap-exception-handling
  [handler]
  (fn [request]
    (try
      (handler request)
      (catch Exception e
        (gen-error 400 "Los datos enviados no son válidos")))))

(def app
  (-> (handler/api app-routes)
      wrap-exception-handling))

(defn -main
  [& [port]]
  (let [port (Integer. (or port
                           (System/getenv "PORT")
                           5000))]
    (jetty/run-jetty #'app {:port  port
                            :join? false})))
