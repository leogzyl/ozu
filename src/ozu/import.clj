(ns ozu.import
  (:require [clojure.string :as str])
  (:require [clojure.java.io :as io])
  (:require ozu.persistence)
  (:require ozu.markov)
  (:gen-class))

(def source-filename "resources/data/import/15codmun.csv")

(defn read-csv []
  (with-open [rdr (io/reader source-filename)]
    (let [lseq (line-seq rdr)
          header (first lseq)
          hkeys (map keyword (str/split header #";"))]
      (loop [data (rest lseq) result []]
        (if (not (seq data))
          result
          (recur (rest data) (conj result (zipmap hkeys (str/split (first data) #";")))))))))

(defn keep-as-set [records keys]
   (into #{} (map #(into {} (for [[k v] % :when (some (fn [a] (= a k)) keys)] [k v])) records )))

(defn get-ccaa [records]
   (keep-as-set records [:ca_cod :ca_desc]))

(defn destraduce [nombre]
  (str/trim (first (str/split nombre #"[/-]"))))

(defn desarticulo [nombre]
  (str/trim (str/replace nombre #"(.*),(.*)" "$2 $1")))

(defn desapostrofe [nombre]
  (str/replace nombre  #"(.*),\s?([lL]')" "$2$1"))

(defn get-clean-val [map-entry]
  (-> map-entry :mun_desc destraduce desapostrofe desarticulo))

(defn get-text-block [m]
  (let [munvals (map get-clean-val m)]
    (str/join "\n" munvals)))

(defn import-all []
  (let [records (read-csv)
        ccaa (get-ccaa records)]
    (ozu.persistence/write-ccaa-records ccaa)
    (doseq [ca-cod (cons nil (map :ca_cod ccaa))
            n (range 1 5)]
      (let [ca-records (if (nil? ca-cod) records (filter #(= (:ca_cod %) ca-cod) records))
            ca-block (get-text-block ca-records)
            ca-freqs (ozu.markov/gen-freq ca-block n)]
        (ozu.persistence/write-freq-records ca-freqs ca-cod n)))))
