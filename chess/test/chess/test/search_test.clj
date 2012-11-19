(ns chess.test.search-test
  (:use [chess.core])
  (:use [chess.bitboard])
  (:use [chess.move])
  (:use [chess.search])
  (:use [clojure.test])
  (:use [midje.sweet]))


;; Thes are perf tests
;; http://chessprogramming.wikispaces.com/Perft+Results
;; TODO: make them return counts

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
  (perftest board 3 :nWhite) => 2812) ; Failing!
  ; The 28 bit of :nBlack is set, but no piece-type for this bit is set? Hmmm

  ;(perftest board 4 :nWhite) => 43238
  ;(perftest board 5 :nWhite) => 674624)

(fact
  "Perft for position 4"
  (def board (ref (create-board)))
  ;; removing black pieces
  (make-move board 0 52)
  (make-move board 52 0)
;make-move :nBlack 24 :nBishop 17 :nQueen true
  ; setup board
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
  (perftest board 3 :nWhite) => 9467) ; Failing!
  ;(perftest board 4 :nWhite) => 422333
  ;(perftest board 5 :nWhite) => 15833292)




;*** before make-move

;BR :   :   :   :BK :   :   :BR :
;WP :BP :BP :BP :   :BP :BP :BP :
   ;:BB :   :   :   :BN :BB :WN :
;BN :WP :WP :   :   :   :   :   :
;WB :WB :   :   :WP :   :   :   :
;BQ :   :   :   :   :WN :   :   :
;WP :BP :   :WP :   :   :WP :WP :
;WR :   :   :WQ :   :WR :WK :   :

;submoves #<Atom@16fbcb70: ()>
;*** after make-move

;BR :   :   :   :BK :   :   :BR :
;WP :BP :BP :BP :   :BP :BP :BP :
   ;:BB :   :   :   :BN :BB :WN :
;BN :WP :WP :   :   :   :   :   :
;WB :WB :   :   :WP :   :   :   :
   ;:BQ :   :   :   :WN :   :   :
;WP :BP :   :WP :   :   :WP :WP :
;WR :   :   :WQ :   :WR :WK :   :

;submoves #<Atom@16fbcb70: ({:color :nBlack, :from 16, :fromKey :nQueen, :to 17, :toKey nil, :capture false})>
;*** before unmake-move

;BR :   :   :   :BK :   :   :BR :
;WP :BP :BP :BP :   :BP :BP :BP :
   ;:BB :   :   :   :BN :BB :WN :
;BN :WP :WP :   :   :   :   :   :
;BB :WB :   :   :WP :   :   :   :
   ;:WQ :   :   :   :WN :   :   :
;WP :BP :   :WP :   :   :WP :WP :
;WR :   :   :WQ :   :WR :WK :   :

;submoves #<Atom@16fbcb70: ({:color :nBlack, :from 16, :fromKey :nQueen, :to 17, :toKey nil, :capture false})>
;*** after unmake-move

;BR :   :   :   :BK :   :   :BR :
;WP :BP :BP :BP :   :BP :BP :BP :
   ;:BB :   :   :   :BN :BB :WN :
;BN :WP :WP :   :   :   :   :   :
;BB :WB :   :   :WP :   :   :   :
;BQ :   :   :   :   :WN :   :   :
;WP :BP :   :WP :   :   :WP :WP :
;WR :   :   :WQ :   :WR :WK :   :
