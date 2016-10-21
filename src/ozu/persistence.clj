(ns ozu.persistence
  (:require [clojure.java.io :as io])
  (:require [clojure.edn :as edn]))

(def store-path "data/store/")
(def ccaa-path (str store-path "ccaa/ccaa.edn"))
(def freq-path (str store-path "frequencies/"))

(defn recursive-delete [directory]
  (if (.isDirectory directory)
    (when (reduce #(and %1 (recursive-delete %2)) true (.listFiles directory))
      (.delete directory))
    (.delete directory)))

(defn clear-store []
  (recursive-delete (io/as-file (str "resources/" store-path))))

(defn write-ccaa-records [records]
  (let [ca-filename (str "resources/" ccaa-path)]
    (io/make-parents ca-filename)
    (spit ca-filename records)))

(defn write-freq-records [ca-freqs ca-cod n]
  (let [freq-filename (str "resources/" freq-path ca-cod n ".edn")]
    (io/make-parents freq-filename)
    (spit freq-filename ca-freqs)))

(defn read-freqs [ca-cod n]
  (let [filename (str freq-path ca-cod n ".edn")]
    (edn/read-string (slurp (io/resource filename)))))

(defn read-ccaa []
  (let [filename ccaa-path]
    (edn/read-string (slurp (io/resource filename)))))
