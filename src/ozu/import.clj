;;TODO Procesar descripción de municipio
(defn read-csv [filename]
  (with-open [rdr (clojure.java.io/reader filename)]
    (let [lseq (line-seq rdr)
          header (first lseq)
          hkeys (map keyword (clojure.string/split header #";"))]
      (loop [data (rest lseq) result []]
        (if (not (seq data))
          result
          (recur (rest data) (conj result (zipmap hkeys (clojure.string/split (first data) #";")))))))))

(defn destraduce [nombre]
  (clojure.string/trim (first (clojure.string/split nombre #"[/-]"))))

(defn desarticulo [nombre]
  (clojure.string/trim (clojure.string/replace nombre  #"(.*),(.*)" "$2 $1")))

(defn desapostrofe [nombre]
  (clojure.string/replace nombre  #"(.*),\s?([lL]')" "$2$1"))

;; (def munmap (read-csv "data.csv"))
;;
;; (def munblock (clojure.string/join "\n" (map :mun_desc munmap)))
;; (def munblock (clojure.string/join "\n" (map #(-> % :mun_desc destraduce desapostrofe desarticulo) munmap)))
