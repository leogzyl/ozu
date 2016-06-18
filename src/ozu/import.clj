(ns ozu.import
  (:gen-class))

(def source-filename "resources/data/import/15codmun.csv")
(def freq-path "resources/data/store/frequencies/")

(defn read-csv []
  (with-open [rdr (clojure.java.io/reader source-filename)]
    (let [lseq (line-seq rdr)
          header (first lseq)
          hkeys (map keyword (clojure.string/split header #";"))]
      (loop [data (rest lseq) result []]
        (if (not (seq data))
          result
          (recur (rest data) (conj result (zipmap hkeys (clojure.string/split (first data) #";")))))))))

(defn keep-as-set [records keys]
   (into #{} (map #(into {} (for [[k v] % :when (some (fn [a] (= a k)) keys)] [k v])) records )))

(defn get-ccaa [records]
   (keep-as-set records [:ca_cod :ca_desc]))

(defn destraduce [nombre]
  (clojure.string/trim (first (clojure.string/split nombre #"[/-]"))))

(defn desarticulo [nombre]
  (clojure.string/trim (clojure.string/replace nombre #"(.*),(.*)" "$2 $1")))

(defn desapostrofe [nombre]
  (clojure.string/replace nombre  #"(.*),\s?([lL]')" "$2$1"))

(defn get-clean-val [map-entry]
  (-> map-entry :mun_desc destraduce desapostrofe desarticulo))

(defn get-text-block [m]
  (let [munvals (map get-clean-val m)]
    (clojure.string/join "\n" munvals)))

(defn map-vals [m f]
  (into {} (for [[k v] m] [k (f v)])))

(defn gen-freq [textblock token-length]
  (loop [s textblock m {}]
    (if (not (seq s))
      (map-vals m #(frequencies %))
      (let [l (inc token-length)
            chunk (take l s)
            reached-end (< (count chunk) l)
            token (apply str (take token-length chunk))
            chr (if reached-end \newline (last chunk))
            step (if (= chr \newline) l 1)
            newmap (assoc m token (str (m token "") chr))]
        (recur (nthrest s step) newmap)))))


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
            ca-freqs (gen-freq ca-block n)
            freq-filename (str "resources/data/store/frequencies/" ca-cod n ".edn")]
        (spit freq-filename ca-freqs)))))
