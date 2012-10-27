(ns chess.bitboard)

(defn create-board 
  "Creates a new chess bitboard."
  []
  {:nWhite     (biginteger 2r0000000000000000000000000000000000000000000000001111111111111111),
   :nBlack     (biginteger 2r1111111111111111000000000000000000000000000000000000000000000000),
   :nPawn      (biginteger 2r0000000011111111000000000000000000000000000000001111111100000000),
   :nKnight    (biginteger 2r0100001000000000000000000000000000000000000000000000000001000010),
   :nBishop    (biginteger 2r0010010000000000000000000000000000000000000000000000000000100100),
   :nRook      (biginteger 2r1000000100000000000000000000000000000000000000000000000010000001),
   :nQueen     (biginteger 2r0000100000000000000000000000000000000000000000000000000000001000),
   :nKing      (biginteger 2r0001000000000000000000000000000000000000000000000000000000010000),
   :moves-made '()})

(def ^{:private true}
  board-array-string
  (make-array String 64))

(defn print-board
  "Prints a chess board."
  [board]
  (let [wp (.and (:nWhite board) (:nPawn board))
        bp (.and (:nBlack board) (:nPawn board))
        wn (.and (:nWhite board) (:nKnight board))
        bn (.and (:nBlack board) (:nKnight board))
        wb (.and (:nWhite board) (:nBishop board))
        bb (.and (:nBlack board) (:nBishop board))
        wr (.and (:nWhite board) (:nRook board))
        br (.and (:nBlack board) (:nRook board))
        wq (.and (:nWhite board) (:nQueen board))
        bq (.and (:nBlack board) (:nQueen board))
        wk (.and (:nWhite board) (:nKing board))
        bk (.and (:nBlack board) (:nKing board))]
    (loop [i 63]
      (when (>= i 0)
        ; Populating board-array-string
        (aset board-array-string i 
          (cond
            (.testBit wp i) "WP" 
            (.testBit bp i) "BP" 
            (.testBit wn i) "WN" 
            (.testBit bn i) "BN" 
            (.testBit wb i) "WB" 
            (.testBit bb i) "BB" 
            (.testBit wr i) "WR" 
            (.testBit br i) "BR" 
            (.testBit wq i) "WQ" 
            (.testBit bq i) "BQ" 
            (.testBit wk i) "WK" 
            (.testBit bk i) "BK" 
            :else "  "))
        ; Doing actual print of board
        (if (= (mod i 8) 0)
          (loop [j 0]
            (when (< j 8)
              (print (aget board-array-string (+ i j)) ":")
              (recur (inc j)))))
        (if (= (mod (+ i 1) 8) 0)
          (print "\n"))
        (recur (dec i)))))
  (println "\n"))

(defn print-big-int [i]
  (loop [j 63]
    (when (>= j 0)
      (print (if (.testBit i j)
                 "1"
                 "0") j ": ")
      (if (= (mod j 8) 0)
        (println "\n"))
      (recur (dec j)))))
