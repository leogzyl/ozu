(ns ozu.webui.views
  (:require [ozu.core :as core]
            [clojure.string :as str]
            [hiccup.page :as hic-p]
            [hiccup.form :as hicf])
  )

(defn gen-page-head
  [title]
  [:head
   [:meta {:charset "utf-8"}]
   [:meta {:http-equiv "X-UA-Compatible" :content "IE=edge"}]
   [:meta {:name "viewport" :content "width=device-width, initial-scale=1"}]
   [:title title]
   (hic-p/include-css "/css/bootstrap.min.css")])

(def header-links
  [:div#header-links
   "[ "
   [:a {:href "/"} "Inicio"]
   " ]"])

(def navbar
  [:nav {:class "navbar navbar-default" :role "navigation"}
   [:div {:class "container"}
    [:div.navbar-header
     [:button {:type "button" :class "navbar-toggle collapsed" :data-toggle "collapse"
               :data-target "#nvcollapse" :aria-expanded "false"}
      [:span {:class "sr-only"} "Toggle"]
      [:span {:class "icon-bar"}]
      [:span {:class "icon-bar"}]
      [:span {:class "icon-bar"}]]
     [:a.navbar-brand {:href "/"} "Ozu!"]]
    [:div {:class "collapse navbar-collapse" :id "nvcollapse"}
     [:ul {:class "nav navbar-nav"}
      [:li [:a {:href "/about"} "Acerca de"]]]]]])

(defn mainform
  [params]
  [:div {:class "container"}
   [:h4 "Generador aleatorio de nombres de pueblos de España"]
   [:form {:action "/" :method "GET"}
    [:div {:class "form-group"}
     [:label {:for "ca-cod"} "Comunidad Autónoma" ]
     [:select {:name "ca-cod" :id "ca-cod" :class "form-control"} "ca"
      (hicf/select-options (ccaa-options) (:ca-cod params))]
     [:label {:for "n"} "Similaridad"]
     [:input {:id "n" :class "form-control" :type "number" :min 1 :max 4 :name "n" :value (:n params 2) :required true}]]
    [:button {:type "submit" :class "btn btn-primary btn-lg"} "Ozu!"]]
    [:h3 (ozu.core/generate (:ca-cod params "") (:n params 2))]])

(def foot
  (list (hic-p/include-js "https://ajax.googleapis.com/ajax/libs/jquery/1.11.3/jquery.min.js")
  (hic-p/include-js "js/bootstrap.min.js")))

(defn ccaa-options []
  (cons ["" nil] (sort (for [r (core/get-ccaa-records)] [(:ca_desc r) (:ca_cod r)]))))

(defn home-page
  [params]
  (hic-p/html5
   (gen-page-head "Inicio")
    navbar
    (mainform params)
    foot))


