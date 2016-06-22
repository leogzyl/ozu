(ns ozu.markov)

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

(defn rand-upper-key [m]
  (let [k (keys m)
        K (filter #(Character/isUpperCase (first %)) k)]
    (nth K (rand-int (count K)))))

(defn weighted-rand-key [m]
  (let [w (reductions + (vals m))
        r (rand-int (last w))]
    (nth (keys m) (count (take-while #( <= % r ) w)))))

(defn walk [m]
  (loop [k (rand-upper-key m)
         rand-char (weighted-rand-key (m k))
         result k]
    (if (= \newline rand-char) ;; or result exceeds a limit?
      result
      (let [new-key (apply str (rest (str k rand-char)))
            new-char (weighted-rand-key (m new-key))]
        (recur new-key new-char (str result rand-char))))))
