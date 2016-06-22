(ns ozu.webui.views
  (:require [ozu.core :as core]
            [clojure.string :as str]
            [hiccup.page :as hic-p]
            [hiccup.form :as hicf]))

(defn gen-page-head
  [title]
  [:head
   [:title "Ozu!"]
   (hic-p/include-css "/css/styles.css")])

(def header-links
  [:div#header-links
   "[ "
   [:a {:href "/"} "Inicio"]
   " ]"])

(defn ccaa-options []
  (cons ["" nil] (sort (for [r (core/get-ccaa-records)] [(:ca_desc r) (:ca_cod r)]))))

(defn home-page
  [params]
  (hic-p/html5
   (gen-page-head "Inicio")
   header-links
   [:h1 "Ozu!"]
   [:p "Generador aleatorio de nombres de pueblos de España"]
   [:form {:action "/" :method "POST"}
    [:p "Comunidad Autónoma: "
     [:select {:name "ca-cod" } "ca"
      (hicf/select-options (ccaa-options) (:ca-cod params))]]
    [:p "Similaridad: "
     [:input {:type "number" :min 1 :max 4 :name "n" :value (:n params 2) :required true}]]
    [:p [:input {:type "submit" :value "Ozu!"}]]]
    [:p [:strong (ozu.core/generate (:ca-cod params "") (:n params 2))]]))


