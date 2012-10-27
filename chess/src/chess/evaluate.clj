(ns chess.evaluate
  (:use chess.bitboard)
  (:use chess.move))

;; Square values from SharpChess
;; https://github.com/PeterHughes/SharpChess/tree/master/SharpChess.Model
(def ^{:private true}
  knight-values
  [1, 1,  1,  1,  1,  1, 1, 1,
   1, 7,  7,  7,  7,  7, 7, 1,
   1, 7, 18, 18, 18, 18, 7, 1,
   1, 7, 18, 27, 27, 18, 7, 1,
   1, 7, 18, 27, 27, 18, 7, 1,
   1, 7, 18, 18, 18, 18, 7, 1,
   1, 7,  7,  7,  7,  7, 7, 1,
   1, 1,  1,  1,  1,  1, 1, 1])

(def ^{:private true}
  bishop-values
  [10, 10, 10, 10, 10, 10, 10, 10, 
   10, 25, 20, 20, 20, 20, 25, 10, 
   10, 49, 30, 30, 30, 30, 49, 10, 
   10, 20, 30, 40, 40, 30, 20, 10, 
   10, 20, 30, 40, 40, 30, 20, 10, 
   10, 49, 30, 30, 30, 30, 49, 10, 
   10, 25, 20, 20, 20, 20, 25, 10, 
   10, 10, 10, 10, 10, 10, 10, 10])

(def ^{:private true}
  king-values
  [1, 1,  1,  1,  1,  1, 1, 1, 
   1, 7,  7,  7,  7,  7, 7, 1, 
   1, 7, 18, 18, 18, 18, 7, 1, 
   1, 7, 18, 27, 27, 18, 7, 1, 
   1, 7, 18, 27, 27, 18, 7, 1, 
   1, 7, 18, 18, 18, 18, 7, 1, 
   1, 7,  7,  7,  7,  7, 7, 1, 
   1, 1,  1,  1,  1,  1, 1, 1])

(def ^{:private true}
  rook-values
  [10, 10, 10, 10, 10, 10, 10, 10, 
   10, 20, 20, 20, 20, 20, 20, 10, 
   10, 20, 30, 30, 30, 30, 20, 10, 
   10, 20, 30, 40, 40, 30, 20, 10, 
   10, 20, 30, 40, 40, 30, 20, 10, 
   10, 20, 30, 30, 30, 30, 20, 10, 
   10, 20, 20, 20, 20, 20, 20, 10, 
   10, 10, 10, 10, 10, 10, 10, 10])

(def ^{:private true}
  pawn-values
  [0, 6, 16, 32, 32, 16, 6, 0])

(defn get-position-value
  "Returns the value of a piece type at a specific position."
  [aKey]
  (assert (keyword? aKey))
  (cond (= aKey :nKing) 900
        (= aKey :nQueen) 250
        (= aKey :nRook) 175
        (= aKey :nBishop) 125
        (= aKey :nKnight) 125
        (= aKey :nPawn) 50))

(defn evaluate
  "Evaluates a board."
  [board pov]
  1)
;(defn evaluate
  ;"Evaluates a board."
  ;[board pov]
  ;(loop [total 0
         ;povBits  (pov @board)
         ;currBit  (.getLowestSetBit povBits)
         ;currType (get-piece-type @board currBit)
         ;pVal     (get-position-value currType)]
    ;(assert (keyword? currType))
    ;(if (and (pos? povBits) (pos? currBit))
      ;(let [nextPOV    (.clearBit povBits currBit)
            ;nextCurr   (.getLowestSetBit nextPOV)
            ;nextType   (get-piece-type @board nextCurr)]
        ;(recur (+ total pVal)
               ;nextPOV
               ;nextCurr
               ;nextType
               ;(get-position-value nextType)))
      ;(+ total pVal))))
