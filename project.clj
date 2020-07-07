(defproject com.frosku/stables "0.1.0"
  :author "Frosku <frosku@frosku.com>"
  :signing {:gpg-key "frosku@frosku.com"}
  :description "Library for the procedural generation of
                pastel-colored ponies."
  :url "https://github.com/Frosku/stables"
  :license {:name "The Unlicense"
            :url "https://unlicense.org"}
  :dependencies [[org.clojure/clojure "1.10.1"]
                 [clojure2d "1.3.1"]
                 [com.frosku/disultory "0.1.4"]]
  :repl-options {:init-ns stables.core}
  :source-paths ["src"]
  :test-paths ["t"]
  :target-path "target/%s/"
  :compile-path "%s/classes"
  :plugins [[lein-bump-version "0.1.6"]]
  :clean-targets ^{:protect false} [:target-path])
