(ns chess.search
  (:use chess.bitboard)
  (:use chess.move)
  (:use chess.evaluate)
  (:use chess.util))

(defn negaMax [board depth pov]
  (assert (keyword? pov))
  (debug (str "negaMax depth : " depth " pov: " best) (:fine debugLevels))
  (if (<= depth 0)
    (evaluate board pov)



    (loop [moves (get-move board pov)
           best (Integer/MIN_VALUE)]
      (debug (str "negaMax moves : " moves " best: " best) (:fine debugLevels))




      (if (seq moves)
        (do 
          (debug (str "negaMax making move from : " (:from (first moves))
                      " to : " (:to (first moves))
                      " board : " board) (:fine debugLevels))

          (make-move board (:from (first moves)) (:to (first moves)))

          (debug (str "negaMax board : " board) (:fine debugLevels))

          (let [aVal (- (negaMax (dec depth) board pov))]

            (debug "negaMax going to un-make" (:fine debugLevels))
            (unmake-move board)
            (debug "negaMax going to un-made" (:fine debugLevels))

            (recur (rest moves)
                   (if (> aVal best)
                        aVal
                        best))))
        best))))

(defn perftest [board depth pov]
  (assert (keyword? pov))
  (loop [moves (get-legal-move board pov)
         node-count 0
         local-moves (atom '())]
    (if (and (seq moves) (> depth 1))
      (do

          (debug "perftest *** before make-move" (:fine debugLevels))
          (debug (str "perftest board" @board) (:fine debugLevels))
          (debug (str "perftest piece type at 17" (get-piece-type @board 17))
                 (:fine debugLevels))
          (debug (str "perftest local-moves" local-moves) (:fine debugLevels))

          (debug (str "perftest local-moves : " local-moves) (:fine debugLevels))
          (debug (str "perftest board : " @board) (:fine debugLevels))

          (make-move board (:from (first moves)) (:to (first moves)) local-moves)

          (debug (str "perftest *** after make-move : " @board) (:fine debugLevels))
          (debug (str "perftest piece type at 17 : " (get-piece-type @board 17))
                 (:fine debugLevels))
          (debug (str "perftest sub local-moves : " local-moves) (:fine debugLevels))

        (let [subMoves (perftest board (dec depth) (get-opposing-color pov))]

          (debug "perftest *** before unmake-move" (:fine debugLevels))
          (debug (str "perftest board : " @board) (:fine debugLevels))
          (debug (str "perftest piece type at 17 : " (get-piece-type @board 17))
                 (:fine debugLevels))
          (debug (str "perftest sub local-moves : " local-moves) (:fine debugLevels))

          (unmake-move board local-moves)

          (debug "perftest *** after unmake-move" (:fine debugLevels))
          (debug (str "perftest board : " @board) (:fine debugLevels))
          (debug (str "perftest piece type at 17 : " (get-piece-type @board 17))
                 (:fine debugLevels))
          (debug (str "perftest sub local-moves : " local-moves) (:fine debugLevels))

          (recur (rest moves)
                 (+ node-count subMoves)
                 local-moves)))
      (+ node-count (count moves)))))
