(ns chess.test.core
  (:use [chess.core])
  (:use [chess.bitboard])
  (:use [chess.move])
  (:use [clojure.test])
  (:use [midje.sweet]))

(defn note-expected-failure [] (println "^^^^ The previous failure was expected ^^^^"))

;; Failing tests should look familiar:
;;     FAIL at (core_test.clj:34)
;;     Expected: 3
;;       Actual: 4

;(fact ( #(+ 1 %) 3) => 3) (note-expected-failure)
