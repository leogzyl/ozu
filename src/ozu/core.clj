(ns ozu.core
  (:gen-class)
  (:require ozu.markov)
  (:require ozu.persistence))

(defn generate
  ([]
   (generate 2))
  ([n]
   (generate "" n))
  ([ca-cod n]
   (ozu.markov/walk (ozu.persistence/read-freqs ca-cod n))))

(defn get-ccaa-records []
  (ozu.persistence/read-ccaa))
