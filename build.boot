(set-env!
  :source-paths #{"src"}
  :dependencies '[[org.clojure/clojure "1.8.0"]
                  [crisptrutski/boot-cljs-test "0.3.0"]
                  [adzerk/boot-cljs "1.7.228-2"]
                  [org.clojure/clojurescript "1.7.228"]
                  [doo "0.1.7"]])

(require '[adzerk.boot-cljs :refer [cljs]])
(require '[crisptrutski.boot-cljs-test :refer [test-cljs]])

(deftask testing [] (merge-env! :source-paths #{"test"}) identity)
