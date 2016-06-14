(ns ozu.core
  (:gen-class))

(defn -main
  "I don't do a whole lot ... yet."
  [& args]
  (println "Hello, World!"))

(def filename "resources/data/import/15codmun.csv")
(def freq-path "resources/data/store/frequencies/")

(defn read-csv []
  (with-open [rdr (clojure.java.io/reader filename)]
    (let [lseq (line-seq rdr)
          header (first lseq)
          hkeys (map keyword (clojure.string/split header #";"))]
      (loop [data (rest lseq) result []]
        (if (not (seq data))
          result
          (recur (rest data) (conj result (zipmap hkeys (clojure.string/split (first data) #";")))))))))

(defn keep-as-set [m keys]
   (into #{} (map #(into {} (for [[k v] % :when (some (fn [a] (= a k)) keys)] [k v])) m )))

(defn get-ccaa [m]
   (keep-as-set m [:ca_cod :ca_desc]))

(defn destraduce [nombre]
  (clojure.string/trim (first (clojure.string/split nombre #"[/-]"))))

(defn desarticulo [nombre]
  (clojure.string/trim (clojure.string/replace nombre #"(.*),(.*)" "$2 $1")))

(defn desapostrofe [nombre]
  (clojure.string/replace nombre  #"(.*),\s?([lL]')" "$2$1"))

(defn get-clean-val [entry]
  (-> entry :mun_desc destraduce desapostrofe desarticulo))

(defn get-text-block [m]
  (let [munvals (map get-clean-val m)]
    (clojure.string/join "\n" munvals)))

(defn map-vals [m f]
  (into {} (for [[k v] m] [k (f v)])))

(defn gen-freq2 [s m token-length]
  (if (not (seq s))
    (map-vals m #(frequencies %))
    (let [token (apply str (take token-length s))
          chr (last (take (inc token-length) s))
          step (if (= chr \newline) (inc token-length) 1)]
      (recur (nthrest s step) (assoc m token (str (m token "") chr)) token-length))))

(defn import-all []
  (let [records (read-csv)
        ccaa (get-ccaa records)
        ca-filename "resources/data/store/ccaa/ccaa.end"
        dummy-freq-filename (str freq-path "dummy.edn")]
    (clojure.java.io/make-parents ca-filename)
    (clojure.java.io/make-parents dummy-freq-filename)
    (spit ca-filename ccaa)
    (doseq [ca-cod (cons nil (map :ca_cod ccaa))
            n (range 1 5)]
      (let [ca-records (if (nil? ca-cod) records (filter #(= (:ca_cod %) ca-cod) records))
            ca-block (get-text-block ca-records)
            ca-freqs (gen-freq2 ca-block {} n)
            freq-filename (str "resources/data/store/frequencies/" ca-cod n ".edn")]
        (spit freq-filename ca-freqs)))))



(defn weighted-rand-key [m]
  (let [w (reductions + (vals m))
        r (rand-int (last w))]
    (nth (keys m) (count (take-while #( <= % r ) w)))))

(defn rand-upper-key [m]
  (let [k (keys m)
        K (filter #(Character/isUpperCase (first %)) k)]
    (nth K (rand-int (count K)))))


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

;; TODO validation?
(defn read-freqs [ca-cod n]
  (let [filename (str freq-path ca-cod n ".edn")]
    (clojure.edn/read-string (slurp filename))))
