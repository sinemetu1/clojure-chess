(ns chess.core
  (:use chess.bitboard)
  (:use chess.move)
  (:use chess.search)
  (:use chess.evaluate))

;(defn -main [& args]
  ;(print-board (create-board))
