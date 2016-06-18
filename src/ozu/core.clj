(ns ozu.core
  (:use ozu.import)
  (:gen-class))

(defn weighted-rand-key [m]
  (let [w (reductions + (vals m))
        r (rand-int (last w))]
    (nth (keys m) (count (take-while #( <= % r ) w)))))

(defn rand-upper-key [m]
  (let [k (keys m)
        K (filter #(Character/isUpperCase (first %)) k)]
    (nth K (rand-int (count K)))))


(defn walk2 [m]
  (loop [k (rand-upper-key m)
         rand-char (weighted-rand-key (m k))
         result k]
    (if (= \newline rand-char) ;; or result exceeds a limit?
      result
      (let [new-key (apply str (rest (str k rand-char)))]
        (recur new-key (weighted-rand-key (m new-key)) (str result rand-char))))))

;; TODO validation?
(defn read-freqs [ca-cod n]
  (let [filename (str freq-path ca-cod n ".edn")]
    (clojure.edn/read-string (slurp filename))))

(defn -main
  ([]
   (-main ""))
  ([n]
   (-main "" n))
  ([ca-cod n]
   (println ca-cod n)
   (println (walk2 (read-freqs ca-cod n)))))
