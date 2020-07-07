(ns stables.color
  (:require [clojure2d.color :as c2d]))

(def ^:const pastel-max-sat 0.38)
(def ^:const pastel-value 0.79)
(def ^:const shadow-hue-offset 0)
(def ^:const shadow-sat-offset 0.13)
(def ^:const shadow-val-offset -0.1)
(def ^:const shadow-coolness 0.25)
(def ^:const highlight-hue-offset 0)
(def ^:const highlight-sat-offset -0.05)
(def ^:const highlight-val-offset 0.1)
(def ^:const highlight-warmth 0.25)

(defn random-hue
  "Generate a random hue between 0 and 255.

   (random-hue)
   => 199"
  []
  (rand-int 256))

(defn random-palette
  "Given an angle by which to offset subsequent hues, a number of colors, a
   max saturation and a value, generate a HSV palette.

   (random-palette 20 3 0.4 0.7)
   => [[120 0.36 0.7] [140 0.32 0.7] [160 0.29 0.7]]"
  [angle colors max-sat value]
  (loop [current-hue (random-hue)
         remaining colors
         acc []]
    (if (= 0 remaining)
      (mapv (fn [hue] [hue (+ (/ max-sat 2) (rand (/ max-sat 2))) value]) acc)
      (recur (mod (+ current-hue angle) 255)
             (dec remaining)
             (conj acc (int current-hue))))))

(defn random-complementary-palette
  "Generate a HSV palette of two colors opposite on the color wheel, with
   variation on a specified max saturation (up to 50% below) and a fixed
   value.

  (random-complementary-palette 0.2 0.7)
  => [[216 0.13 0.7] [88 0.17 0.7]]"
  [max-sat value]
  (random-palette (/ 255 2) 2 max-sat value))

(defn random-triadic-palette
  "Generate a HSV palette of three colors equidistant on the color wheel,
   with variation on a specified max saturation (up to 50% below) and a
   fixed value.

   (random-triadic-palette 0.2 0.8)
   => [[250 0.15 0.8] [80 0.15 0.8] [165 0.19 0.8]]"
  [max-sat value]
  (random-palette (/ 255 3) 3 max-sat value))

(defn random-analogous-palette
  "Generate a HSV palette of three colors each one-ninth the color wheel
   apart, occupying a single third of the color wheel, with variation on a
   specified max saturation (up to 50% below) and a fixed value.

   (random-analogous-palette 0.3 0.75)
   => [[76 0.28 0.75] [48 0.20 0.75] [104 0.22 0.75]]"
  [max-sat value]
  (let [palette (random-palette (/ 255 9) 3 max-sat value)]
    [(nth palette 1) (nth palette 0) (nth palette 2)]))

(defn offset-palette
  "Given a base palette, generates a HSV palette which is offset from that
   palette by a specified hue, saturation, and value. Hues wrap around and
   saturation/value above 1 are fixed to 1.

   (offset-palette 10 0.2 0.3 [[66 0.2 0.4] [91 0.3 0.4]])
   => [[76 0.4 0.7] [101 0.5 0.7]]"
  [hue-variance sat-variance val-variance palette]
  (loop [current-color (first palette)
         more-colors (rest palette)
         acc []]
    (if (empty? current-color)
      (->> acc (mapv (fn [[h s v]] [h (if (< s 1) s 1) (if (< v 1) v 1)])))
      (let [hue (nth current-color 0)
            sat (nth current-color 1)
            value (nth current-color 2)]
        (recur (first more-colors)
               (rest more-colors)
               (conj acc [(-> hue-variance (+ hue) (int) (mod 255))
                          (-> sat-variance (+ sat))
                          (-> val-variance (+ value))]))))))

(defn shadows-for-palette
  "Takes a HSV palette and generates shadows for it, returning a HSV
   palette which is darker & more saturated. Return format is the
   same as offset-palette."
  [palette]
  (offset-palette shadow-hue-offset
                  shadow-sat-offset
                  shadow-val-offset
                  palette))

(defn highlights-for-palette
  "Takes a HSV palette and generates highlights for it, returning a HSV
   palette which is lighter & less saturated. Return format is the same
   as offset palette."
  [palette]
  (offset-palette highlight-hue-offset
                  highlight-sat-offset
                  highlight-val-offset
                  palette))

(defn ->rgb-with-highlights-and-shadows
  "Takes a HSV palette and generates cooler/darker shadows and warmer/
   brighter highlights as extra palettes. IMPORTANT: Returns RGB
   palettes, not HSV palettes.

   (with-shadows-and-highlights [[66 0.2 0.4] [91 0.3 0.4]])
   => {:base [#vec4 [99.96000000000001 ...]]
       :shadows [#vec4 [73.72730355424501 ...]]
       :highlights [#vec4 [141.2446410483278 ...]]}"
  [palette]
  {:base (->> palette (mapv #(c2d/from-HSV %)))
   :shadows (as-> (shadows-for-palette palette) p
              (mapv #(c2d/from-HSV %) p)
              (c2d/adjust-temperature p :cool shadow-coolness))
   :lines (->> palette
               (shadows-for-palette)
               (shadows-for-palette)
               (mapv #(c2d/from-HSV %)))
   :highlights (as-> (highlights-for-palette palette) p
                 (mapv #(c2d/from-HSV %) p)
                 (c2d/adjust-temperature p :warm highlight-warmth))})

(defn random-triadic-pony-palette
  "Generates a triadic RGB pastel palette for use on a pastel pony.
   Output format is the same as ->rgb-with-highlights-and-shadows."
  []
  (-> (random-triadic-palette pastel-max-sat
                             pastel-value)
     (->rgb-with-highlights-and-shadows)))

(defn random-analogous-pony-palette
  "Generates an analogous RGB pastel palette for use on a pastel pony.
   Output format is the same as ->rgb-with-highlights-and-shadows."
  []
  (-> (random-analogous-palette pastel-max-sat
                               pastel-value)
     (->rgb-with-highlights-and-shadows)))

(defn random-complementary-pony-palette
  "Generates a complementary RGB pastel palette for use on a pastel pony.
   Output format is the same as ->rgb-with-highlights-and-shadows."
  []
  (-> (random-complementary-palette pastel-max-sat
                                  pastel-value)
     (->rgb-with-highlights-and-shadows)))
