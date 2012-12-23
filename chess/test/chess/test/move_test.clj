(ns chess.test.move-test
  (:use [chess.core])
  (:use [chess.bitboard])
  (:use [chess.move])
  (:use [clojure.test])
  (:use [midje.sweet]))

;; Testing make/unmake-move
(fact
  "Testing make-move of white pawn from a2 -> a3"
  (def board (ref (create-board)))
  (make-move board 8 16)
  (:nPawn @board)  => 2r0000000011111111000000000000000000000000000000011111111000000000
  (:nWhite @board) => 2r11111111011111111
  (unmake-move board)
  (:nPawn @board) => 2r0000000011111111000000000000000000000000000000001111111100000000
  (:nWhite @board) => 2r0000000000000000000000000000000000000000000000001111111111111111)

;; Testing make/unmake-move with capture of same piece type
(fact
  "Testing of make-move of white pawn capture of black pawn from a2 -> a7"
  (def board (ref (create-board)))
  (make-move board 8 48)
  (:nPawn @board)  => 2r0000000011111111000000000000000000000000000000001111111000000000
  (:nWhite @board) => 2r0000000000000001000000000000000000000000000000001111111011111111
  (:nBlack @board) => 2r1111111111111110000000000000000000000000000000000000000000000000
  (unmake-move board)
  (:nPawn @board)  => 2r0000000011111111000000000000000000000000000000001111111100000000
  (:nWhite @board) => 2r1111111111111111
  (:nBlack @board) => 2r1111111111111111000000000000000000000000000000000000000000000000)

;; Testing make/unmake-move with capture of different piece type
(fact
  "Testing of make-move of white knight capture of black pawn from a2 -> a7"
  (def board (ref (create-board)))
  (make-move board 1 16)
  (make-move board 16 48)
  (:nPawn @board)   => 2r0000000011111110000000000000000000000000000000001111111100000000
  (:nKnight @board) => 2r0100001000000001000000000000000000000000000000000000000001000000
  (:nWhite @board)  => 2r0000000000000001000000000000000000000000000000001111111111111101
  (:nBlack @board)  => 2r1111111111111110000000000000000000000000000000000000000000000000
  (unmake-move board)
  (:nPawn @board)   => 2r0000000011111111000000000000000000000000000000001111111100000000
  (:nKnight @board) => 2r0100001000000000000000000000000000000000000000010000000001000000
  (:nWhite @board)  => 2r0000000000000000000000000000000000000000000000011111111111111101
  (:nBlack @board)  => 2r1111111111111111000000000000000000000000000000000000000000000000
  (unmake-move board)
  (:nPawn @board)   => 2r0000000011111111000000000000000000000000000000001111111100000000
  (:nKnight @board) => 2r0100001000000000000000000000000000000000000000000000000001000010
  (:nWhite @board)  => 2r0000000000000000000000000000000000000000000000001111111111111111
  (:nBlack @board)  => 2r1111111111111111000000000000000000000000000000000000000000000000)

;; Testing get-move

(fact
  "Testing of get-move for all white pieces."
  (sort-by :to (get-move board :nWhite)) => [{:from 1 :to 16}
                                             {:from 8 :to 16}
                                             {:from 9 :to 17}
                                             {:from 1 :to 18}
                                             {:from 10 :to 18}
                                             {:from 11 :to 19}
                                             {:from 12 :to 20}
                                             {:from 6 :to 21}
                                             {:from 13 :to 21}
                                             {:from 14 :to 22}
                                             {:from 6 :to 23}
                                             {:from 15 :to 23}
                                             {:from 8 :to 24}
                                             {:from 9 :to 25}
                                             {:from 10 :to 26}
                                             {:from 11 :to 27}
                                             {:from 12 :to 28}
                                             {:from 13 :to 29}
                                             {:from 14 :to 30}
                                             {:from 15 :to 31}])

(fact
  "Testing of get-move for all black pieces."
  (sort-by :to (get-move board :nBlack)) => [{:from 48 :to 32}
                                             {:from 49 :to 33}
                                             {:from 50 :to 34}
                                             {:from 51 :to 35}
                                             {:from 52 :to 36}
                                             {:from 53 :to 37}
                                             {:from 54 :to 38}
                                             {:from 55 :to 39}
                                             {:from 48 :to 40}
                                             {:from 57 :to 40}
                                             {:from 49 :to 41}
                                             {:from 50 :to 42}
                                             {:from 57 :to 42}
                                             {:from 51 :to 43}
                                             {:from 52 :to 44}
                                             {:from 53 :to 45}
                                             {:from 62 :to 45}
                                             {:from 54 :to 46}
                                             {:from 55 :to 47}
                                             {:from 62 :to 47}])

;; Testing move north
;; and afile filter
(fact
  "Testing of get-move of white pawn at a2."
  (sort-by :to (get-move board 8)) => [{:from 8 :to 16}
                                       {:from 8 :to 24}])

;; Testing move north
;; and hfile filter
(fact
  "testing of get-move of white pawn at h2."
  (sort-by :to (get-move board 15)) => [{:from 15 :to 23}
                                        {:from 15 :to 31}])

;; Should have only one attack
;; testing afile filter as well
(fact
  "Testing of get-move of white pawn at a7 that can attack."
  (def board (ref (create-board)))
  (make-move board 8 48)
  (get-move board 48) => [{:from 48 :to 57}]
  (unmake-move board))

;; Should have only one attack
;; testing hfile filter as well
(fact
  "Testing of get-move of white pawn at h7 that can attack."
  (def board (ref (create-board)))
  (make-move board 15 55)
  (get-move board 55) => [{:from 55 :to 62}]
  (unmake-move board))

;; Should have two attacks
;; testing forward block as well
(fact
  "Testing of get-move of white pawn at b7 that can attack."
  (def board (ref (create-board)))
  (make-move board 9 49)
  (get-move board 49) => [{:from 49 :to 56} {:from 49 :to 58}]
  (unmake-move board))

;; Testing ray-type moves with Queen Bishop, and Rook
;; Queen
(fact
  "Testing of get-move of white queen at e3 that can attack."
  (def board (ref (create-board)))
  (get-move board 3) => []
  (make-move board 3 20)
  ;; Sorting so that order doesn't matter
  (sort-by :to (get-move board 20)) => [{:from 20 :to 16}
                                        {:from 20 :to 17}
                                        {:from 20 :to 18}
                                        {:from 20 :to 19}
                                        {:from 20 :to 21}
                                        {:from 20 :to 22}
                                        {:from 20 :to 23}
                                        {:from 20 :to 27}
                                        {:from 20 :to 28}
                                        {:from 20 :to 29}
                                        {:from 20 :to 34}
                                        {:from 20 :to 36}
                                        {:from 20 :to 38}
                                        {:from 20 :to 41}
                                        {:from 20 :to 44}
                                        {:from 20 :to 47}
                                        {:from 20 :to 48}
                                        {:from 20 :to 52}]
  (unmake-move board))

(fact
  "Testing of get-move of black queen at a3 that can attack."
  (def board (ref (create-board)))
  (get-move board 59) => []
  (make-move board 59 16)
  (sort-by :to (get-move board 16)) => [{:from 16 :to 8}
                                        {:from 16 :to 9}
                                        {:from 16 :to 17}
                                        {:from 16 :to 18}
                                        {:from 16 :to 19}
                                        {:from 16 :to 20}
                                        {:from 16 :to 21}
                                        {:from 16 :to 22}
                                        {:from 16 :to 23}
                                        {:from 16 :to 24}
                                        {:from 16 :to 25}
                                        {:from 16 :to 32}
                                        {:from 16 :to 34}
                                        {:from 16 :to 40}
                                        {:from 16 :to 43}]
  (unmake-move board))

;; bishop
(fact
  "testing of get-move of white bishop at c3 that can attack."
  (def board (ref (create-board)))
  (get-move board 2) => []
  (make-move board 2 18)
  (sort-by :to (get-move board 18)) => [{:from 18 :to 25}
                                        {:from 18 :to 27}
                                        {:from 18 :to 32}
                                        {:from 18 :to 36}
                                        {:from 18 :to 45}
                                        {:from 18 :to 54}]
  (unmake-move board))

(fact
  "Testing of get-move of white bishop at f3 that can attack."
  (def board (ref (create-board)))
  (get-move board 5) => []
  (make-move board 5 21)
  (sort-by :to (get-move board 21)) => [{:from 21 :to 28}
                                        {:from 21 :to 30}
                                        {:from 21 :to 35}
                                        {:from 21 :to 39}
                                        {:from 21 :to 42}
                                        {:from 21 :to 49}]
  (unmake-move board))

;; Rook
(fact
  "Testing of get-move of white rook at a3 that can attack."
  (def board (ref (create-board)))
  (get-move board 0) => []
  (make-move board 0 16)
  (sort-by :to (get-move board 16)) => [{:from 16 :to 17}
                                        {:from 16 :to 18}
                                        {:from 16 :to 19}
                                        {:from 16 :to 20}
                                        {:from 16 :to 21}
                                        {:from 16 :to 22}
                                        {:from 16 :to 23}
                                        {:from 16 :to 24}
                                        {:from 16 :to 32}
                                        {:from 16 :to 40}
                                        {:from 16 :to 48}]
  (unmake-move board))

(fact
  "Testing of get-move of white rook at h3 that can attack."
  (def board (ref (create-board)))
  (get-move board 7) => []
  (make-move board 7 23)
  (sort-by :to (get-move board 23)) => [{:from 23 :to 16}
                                        {:from 23 :to 17}
                                        {:from 23 :to 18}
                                        {:from 23 :to 19}
                                        {:from 23 :to 20}
                                        {:from 23 :to 21}
                                        {:from 23 :to 22}
                                        {:from 23 :to 31}
                                        {:from 23 :to 39}
                                        {:from 23 :to 47}
                                        {:from 23 :to 55}]
  (unmake-move board))

(fact
  "Testing of get-move of black rook at b6 that can attack."
  (def board (ref (create-board)))
  (get-move board 56) => []
  (make-move board 56 41)
  (sort-by :to (get-move board 41)) => [{:from 41 :to 9}
                                        {:from 41 :to 17}
                                        {:from 41 :to 25}
                                        {:from 41 :to 33}
                                        {:from 41 :to 40}
                                        {:from 41 :to 42}
                                        {:from 41 :to 43}
                                        {:from 41 :to 44}
                                        {:from 41 :to 45}
                                        {:from 41 :to 46}
                                        {:from 41 :to 47}]
  (unmake-move board))

(fact
  "Testing of get-move of black rook at g6 that can attack."
  (def board (ref (create-board)))
  (get-move board 63) => []
  (make-move board 63 46)
  (sort-by :to (get-move board 46)) => [{:from 46 :to 14}
                                        {:from 46 :to 22}
                                        {:from 46 :to 30}
                                        {:from 46 :to 38}
                                        {:from 46 :to 40}
                                        {:from 46 :to 41}
                                        {:from 46 :to 42}
                                        {:from 46 :to 43}
                                        {:from 46 :to 44}
                                        {:from 46 :to 45}
                                        {:from 46 :to 47}]
  (unmake-move board))

;; Knight
(fact
  "Testing of get-move of white knight at b3."
  (def board (ref (create-board)))
  (sort-by :to (get-move board 1)) => [{:from 1 :to 16}
                                       {:from 1 :to 18}]
  (make-move board 1 17)
  (sort-by :to (get-move board 17)) => [{:from 17 :to 27}
                                        {:from 17 :to 32}
                                        {:from 17 :to 34}]
  (unmake-move board))

(fact
  "Testing of get-move of white knight at g3."
  (def board (ref (create-board)))
  (sort-by :to (get-move board 6)) => [{:from 6 :to 21}
                                       {:from 6 :to 23}]
  (make-move board 6 22)
  (sort-by :to (get-move board 22)) => [{:from 22 :to 28}
                                        {:from 22 :to 37}
                                        {:from 22 :to 39}]
  (unmake-move board))

(fact
  "Testing of get-move of white knight at d5 that can attack."
  (def board (ref (create-board)))
  (make-move board 1 35)
  (sort-by :to (get-move board 35)) => [{:from 35 :to 18}
                                        {:from 35 :to 20}
                                        {:from 35 :to 25}
                                        {:from 35 :to 29}
                                        {:from 35 :to 41}
                                        {:from 35 :to 45}
                                        {:from 35 :to 50}
                                        {:from 35 :to 52}]
  (unmake-move board))

(fact
  "Testing of get-move of black knight at d4 that can attack."
  (def board (ref (create-board)))
  (make-move board 57 27)
  (sort-by :to (get-move board 27)) => [{:from 27 :to 10}
                                        {:from 27 :to 12}
                                        {:from 27 :to 17}
                                        {:from 27 :to 21}
                                        {:from 27 :to 33}
                                        {:from 27 :to 37}
                                        {:from 27 :to 42}
                                        {:from 27 :to 44}]
  (unmake-move board))

(fact
  "Testing of get-move of black knight at b6."
  (def board (ref (create-board)))
  (sort-by :to (get-move board 57)) => [{:from 57 :to 40}
                                        {:from 57 :to 42}]
  (make-move board 57 41)
  (sort-by :to (get-move board 41)) => [{:from 41 :to 24}
                                        {:from 41 :to 26}
                                        {:from 41 :to 35}]
  (unmake-move board))

(fact
  "Testing of get-move of black knight at g6."
  (def board (ref (create-board)))
  (sort-by :to (get-move board 62)) => [{:from 62 :to 45}
                                        {:from 62 :to 47}]
  (make-move board 62 46)
  (sort-by :to (get-move board 46)) => [{:from 46 :to 29}
                                        {:from 46 :to 31}
                                        {:from 46 :to 36}]
  (unmake-move board))

;; King
(fact
  "Testing of get-move of white king at d3 and d4."
  (def board (ref (create-board)))
  (get-move board 4) => []
  (make-move board 4 19)
  (sort-by :to (get-move board 19)) => [{:from 19 :to 18}
                                        {:from 19 :to 20}
                                        {:from 19 :to 26}
                                        {:from 19 :to 27}
                                        {:from 19 :to 28}]
  (make-move board 19 27)
  (sort-by :to (get-move board 27)) => [{:from 27 :to 18}
                                        {:from 27 :to 19}
                                        {:from 27 :to 20}
                                        {:from 27 :to 26}
                                        {:from 27 :to 28}
                                        {:from 27 :to 34}
                                        {:from 27 :to 35}
                                        {:from 27 :to 36}]
  (unmake-move board)
  (unmake-move board))

(fact
  "Testing of get-move of white king at a3."
  (def board (ref (create-board)))
  (make-move board 4 16)
  (sort-by :to (get-move board 16)) => [{:from 16 :to 17}
                                        {:from 16 :to 24}
                                        {:from 16 :to 25}]
  (unmake-move board))

(fact
  "Testing of get-move of white king at h3."
  (def board (ref (create-board)))
  (make-move board 4 23)
  (sort-by :to (get-move board 23)) => [{:from 23 :to 22}
                                        {:from 23 :to 30}
                                        {:from 23 :to 31}]
  (unmake-move board))

(fact
  "Testing of get-move of black king at a6."
  (def board (ref (create-board)))
  (make-move board 60 40)
  (sort-by :to (get-move board 40)) => [{:from 40 :to 32}
                                        {:from 40 :to 33}
                                        {:from 40 :to 41}]
  (unmake-move board))

(fact
  "Testing of get-move of black king at h6."
  (def board (ref (create-board)))
  (make-move board 60 47)
  (sort-by :to (get-move board 47)) => [{:from 47 :to 38}
                                        {:from 47 :to 39}
                                        {:from 47 :to 46}]
  (unmake-move board))


;; Testing in-check?
(fact
  "Testing of in-check of white king at e6."
  (def board (ref (create-board)))
  (in-check? board 4) => false
  (make-move board 4 44)
  (in-check? board 44) => true
  (unmake-move board))

(fact
  "Testing of in-check of black king at d3."
  (def board (ref (create-board)))
  (in-check? board 60) => false
  (make-move board 60 19)
  (in-check? board 19) => true
  (unmake-move board))
