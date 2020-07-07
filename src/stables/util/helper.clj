(ns stables.util.helper
  (:require [stables.color :refer :all]
            [clojure2d.extra.utils :as c2deu]))

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
