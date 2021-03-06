(ns chess.test.search-test
  (:use [chess.core])
  (:use [chess.bitboard])
  (:use [chess.move])
  (:use [chess.search])
  (:use [clojure.test])
  (:use [midje.sweet]))

;; Thes are perf tests
;; http://chessprogramming.wikispaces.com/Perft+Results

(fact
  "Perft for starting position"
  (def board (ref (create-board)))
  (perftest board 1 :nWhite) => 20
  (perftest board 2 :nWhite) => 400
  (perftest board 3 :nWhite) => 8902)
  ;(perftest board 4 :nWhite) => 197281
  ;(perftest board 5 :nWhite) => 4865609
  ;(perftest board 6 :nWhite) => 119060324)

(fact
  "Perft for position 2"
  (def board (ref (create-board)))
  ;; board setup for pos 2
  ;; Moving white pieces
  (make-move board 1 18)
  (make-move board 3 21)
  (make-move board 11 35)
  (make-move board 6 36)
  (make-move board 2 11)
  (make-move board 12 28)
  (make-move board 5 12)
  ;; moving black pieces
  (make-move board 57 41)
  (make-move board 49 25)
  (make-move board 52 44)
  (make-move board 54 46)
  (make-move board 55 23)
  (make-move board 62 45)
  (make-move board 59 52)
  (make-move board 58 40)
  (make-move board 61 54)
  
  (perftest board 1 :nWhite) => 48
  (perftest board 2 :nWhite) => 2039)
  ;(perftest board 3 :nWhite) => 97862
  ;(perftest board 4 :nWhite) => 4085603
  ;(perftest board 5 :nWhite) => 193690690)

(fact
  "Perft for position 3"
  (def board (ref (create-board)))
  ;; removing black pieces
  (make-move board 0 48)
  (make-move board 48 49)
  (make-move board 49 52)
  (make-move board 52 54)
  (make-move board 54 55)
  (make-move board 55 56)
  (make-move board 56 57)
  (make-move board 57 58)
  (make-move board 58 59)
  (make-move board 59 61)
  (make-move board 61 62)

  ;; removing white pieces
  (make-move board 50 1)
  (make-move board 1 2)
  (make-move board 2 3)
  (make-move board 3 5)
  (make-move board 5 6)
  (make-move board 6 7)
  (make-move board 7 8)
  (make-move board 8 10)
  (make-move board 10 11)
  (make-move board 11 13)
  (make-move board 13 15)
  ;; setup
  (make-move board 62 25)
  (make-move board 15 50)
  (make-move board 9 33)
  (make-move board 4 32)
  (make-move board 51 43)
  (make-move board 53 29)
  (make-move board 63 39)
  (make-move board 60 31)
  
  (perftest board 1 :nWhite) => 14
  (perftest board 2 :nWhite) => 191
  (perftest board 3 :nWhite) => 2812)
  ;(perftest board 4 :nWhite) => 43238)
  ;(perftest board 5 :nWhite) => 674624)

(fact
  "Perft for position 4"
  (def board (ref (create-board)))
  ;; removing black pieces
  (make-move board 0 52)
  (make-move board 52 0)
  ;; setup board
  (make-move board 1 47)
  (make-move board 2 25)
  (make-move board 6 21)
  (make-move board 5 24)
  (make-move board 4 6)
  (make-move board 7 5)
  (make-move board 9 17)
  (make-move board 48 9)
  (make-move board 17 33)
  (make-move board 13 48)
  (make-move board 10 26)
  (make-move board 12 28)
  (make-move board 57 32)
  (make-move board 59 16)
  (make-move board 61 41)
  (make-move board 58 46)
  (make-move board 62 45)
  
  (perftest board 1 :nWhite) => 6
  (perftest board 2 :nWhite) => 264
  (perftest board 3 :nWhite) => 9467)
  ;(perftest board 4 :nWhite) => 422333
  ;(perftest board 5 :nWhite) => 15833292)

(fact
  "Perft 4 bug"
  (def board (ref {:nKing (biginteger 1152921504606847040),
                   :nPawn (biginteger 67272545472137984),
                   :moves-made '(),
                   :nQueen (biginteger 131080),
                   :nBlack (biginteger 10515449936391307776),
                   :nWhite (biginteger 422238555785577),
                   :nRook (biginteger 9295429630892703777),
                   :nBishop (biginteger 72567817764864),
                   :nKnight (biginteger 175926157508608)}))
  ;; make sure that the white pawn at 8 does not contain a move from 8 to 24
  ;;  ie. attacking WB with WP
  (sort-by :to (get-move board 8)) => [{:from 8, :to 16}
                                       {:from 8, :to 17}])
