(ns chess.test.bitboard_test
  (:use [chess.core])
  (:use [chess.bitboard])
  (:use [chess.move])
  (:use [clojure.test])
  (:use [midje.sweet]))

(def board (create-board))

;; Testing piece positions for correct board setup
(fact "Testing white pieces: Pawn" 
      (.and (:nWhite board) (:nPawn board))
      => 2r1111111100000000)

(fact "Testing white pieces: Bishop"
      (.and (:nWhite board) (:nBishop board))
      => 2r00100100)

(fact "Testing white pieces: Knight"
      (.and (:nWhite board) (:nKnight board))
      => 2r01000010)

(fact "Testing white pieces: Rook"
      (.and (:nWhite board) (:nRook board))
      => 2r10000001)

(fact "Testing white piece: Queen" 
      (.and (:nWhite board) (:nQueen board))
      => 2r00001000)

(fact "Testing white pieces: King"
      (.and (:nWhite board) (:nKing board))
      => 2r00010000)

(fact "Testing black pieces: Pawn" 
      (.and (:nBlack board) (:nPawn board))
      => 2r0000000011111111000000000000000000000000000000000000000000000000)

(fact "Testing black pieces: Bishop"
      (.and (:nBlack board) (:nBishop board))
      => 2r0010010000000000000000000000000000000000000000000000000000000000)

(fact "Testing black pieces: Knight"
      (.and (:nBlack board) (:nKnight board))
      => 2r0100001000000000000000000000000000000000000000000000000000000000)

(fact "Testing black pieces: Rook"
      (.and (:nBlack board) (:nRook board))
      => 2r1000000100000000000000000000000000000000000000000000000000000000)

(fact "Testing black piece: Queen" 
      (.and (:nBlack board) (:nQueen board))
      => 2r0000100000000000000000000000000000000000000000000000000000000000)

(fact "Testing black pieces: King"
      (.and (:nBlack board) (:nKing board))
      => 2r0001000000000000000000000000000000000000000000000000000000000000)
