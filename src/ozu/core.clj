(ns ozu.core
  (:require ozu.markov)
  (:require ozu.persistence)
  (:gen-class))

(defn -main
  ([]
   (-main 2))
  ([n]
   (-main "" n))
  ([ca-cod n]
   (println (ozu.markov/walk (ozu.persistence/read-freqs ca-cod n)))))
