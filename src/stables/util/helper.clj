(ns stables.util.helper
  (:require [stables.color :refer :all]
            [clj-uuid :as uuid]
            [clojure2d.color :as c]
            [clojure2d.pixels :as p]
            [clojure2d.extra.utils :as c2deu]
            [clojure2d.core :as c2d]))

(defn show-palette
  "Given a palette, renders it to a window. This is useful for
   testing new palette generation functions.

   (show-palette)
   => nil"
  [palette]
  (->> (interleave (:base palette)
                   (:base palette)
                   (:base palette)
                   (:base palette)
                   (:shadows palette)
                   (:shadows palette)
                   (:highlights palette))
       (c2deu/palette->image)
       (c2deu/show-image)))

(def eye-white-px (p/load-pixels "res/eyewhite.png"))
(def mane-tail-px (p/load-pixels "res/mane-tail-color.png"))
(def eye-px (p/load-pixels "res/eye-color.png"))
(def body-px (p/load-pixels "res/body-color.png"))
(def lines-px (p/load-pixels "res/lines.png"))
(def mane-tail-lines-px (p/load-pixels "res/mane-tail-lines.png"))

(defn blend-colors-xy
  [f back source x y]
  (let [cb (p/get-color back x y)
        cs (p/get-color source x y)]
    (clojure2d.color.blend/blend-colors f cb cs)))

(defn compose-colors
  [mode p1 p2]
  (p/filter-colors-xy (partial blend-colors-xy
                               (clojure2d.color.blend/blends mode) p1) p2))

(defn uuid
  []
  (-> (uuid/v4)
     (uuid/to-string)))

(defn render-pony
  [palette]
  (let [uuid (uuid)
        base-colors (:base palette)
        pony [[eye-white-px :white]
              [body-px (nth base-colors 0)]
              [eye-px (c/saturate (nth base-colors 2) 0.7)]
              [mane-tail-px (nth base-colors 1)]
              [lines-px (nth (:lines palette) 0)]
              [mane-tail-lines-px (nth (:lines palette) 1)]]]
    (loop [base (first (first pony))
           working (second pony)
           remainder (nthnext pony 2)]
      (if (nil? working)
        (c2d/save base (format "out/%s.png" uuid))
        (recur (compose-colors :normal
                               base
                               (p/filter-channels (p/tint (second working))
                                                  false
                                                  (first working)))
               (first remainder)
               (next remainder))))))

(dotimes [_ 100] (-> (random-triadic-pony-palette)
                    (render-pony)))
