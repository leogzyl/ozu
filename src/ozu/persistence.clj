(ns ozu.persistence
  (:require [clojure.java.io :as io])
  (:require [clojure.edn :as edn])
  (:gen-class))

(def freq-path "resources/data/store/frequencies/")

(defn write-ccaa-records [records]
  (let [ca-filename "resources/data/store/ccaa/ccaa.end"]
    (io/make-parents ca-filename)
    (spit ca-filename records)))

(defn write-freq-records [ca-freqs ca-cod n]
  (let [freq-filename (str freq-path ca-cod n ".edn")]
    (io/make-parents freq-filename)
    (spit freq-filename ca-freqs)))

(defn read-freqs [ca-cod n]
  (let [filename (str freq-path ca-cod n ".edn")]
    (edn/read-string (slurp filename))))
