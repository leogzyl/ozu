(def txt "Texto lagro en Español.\n Tiene líneas y todo.")

(def sq (re-seq #"(?s)." txt))

(defn map-vals [m f]
  (into {} (for [[k v] m] [k (f v)])))

;; TODO intentar implementación con reduce
(defn gen-freq [s m token-length]
  (if (not (seq s))
    (map-vals m #(frequencies %))
    (let [token (apply str (take token-length s))
          chr (last (take (inc token-length) s))]
      (recur (rest s) (assoc m token (str (m token "") chr)) token-length))))

(defn gen-freq2 [s m token-length]
  (if (not (seq s))
    (map-vals m #(frequencies %))
    (let [chnk (apply str (take (inc token-length) s))
          token (subs chnk 0 token-length)
          chr (subs chnk token-length)]
      (recur (rest s) (assoc m token (str (m token "") chr)) token-length))))



;; Luego hacer algo como esto:
;; (j/query db-spec ["SELECT * FROM fruit WHERE cost < ?" 50]
;;   {:result-set-fn (fn [rs]
;;     (reduce (fn [total row-map]
;;                (+ total (:cost row-map)))
;;         0 rs))})
;; produces the total cost of all the cheap fruits
;; La idea es pasar una función similar a gen-freq como :result-set-fn 
;; y devolver el mapa de frecuencias directamente de la query 
;; sin tener que generar secuencias de caracteres:

;;(defn freq-map [ca-cod prov-cod token-length]
;;  (j/query db-spec ["SELECT MUN_DESC FROM MUN WHERE CA_COD = ? AND PROV_COD = ?" ca-cod prov-cod]
;;         {:row-fn :mun_desc
;;          :result-set-fn #(gen-freq (clojure.string/join "\n" %) {} token-length)}))

(defn weighted-rand-key [m]
  (let [w (reductions + (vals m))
        r (rand-int (last w))]
    (nth (keys m) (count (take-while #( <= % r ) w)))))

(defn rand-upper-key [m]
  (let [k (keys m)
        K (filter #(Character/isUpperCase (first %)) k)]
    (nth K (rand-int (count K)))))

;; TODO
;; ¿hace falta BD? Podemos leer las líneas del CSV y generar las frecuencias, 
;; guardarlas en ficheros EDN y recuperar los ficheros a la hora de generar los datos

;;FIXME en el token no puede haber un \n

;;Funciona, no tocar hasta git
(defn walk [m]
  (let [start-val (rand-upper-key m)]
    (loop [s start-val
           nxt-char nil
           result start-val]
      (if (= \newline nxt-char) ;; or result exceeds a limit?
        (subs result 0 (dec (count result)))
        (let [rand-char (weighted-rand-key (m s))
              new-key (apply str (rest (str s rand-char)))]
          (recur new-key rand-char (str result rand-char)))))))

(defn walk2 [m]
  (loop [k (rand-upper-key m) 
         rand-char (weighted-rand-key (m k))
         result k]
    (if (= \newline rand-char) ;; or result exceeds a limit?
      result
      (let [new-key (apply str (rest (str k rand-char)))]
        (recur new-key (weighted-rand-key (m new-key)) (str result rand-char))))))
