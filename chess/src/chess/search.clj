(ns chess.search
  (:use chess.bitboard)
  (:use chess.move)
  (:use chess.evaluate)
  (:use chess.util))

(defn negaMax [board depth pov]
  (assert (keyword? pov))
  (debug (str "negaMax depth : " depth " pov: " pov) (:fine debugLevels))
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
  (debug (str "perftest pov : " pov " depth: " depth " board : " @board) (:coarse debugLevels))
  (loop [moves (get-legal-move board pov)
         node-count 0
         local-moves (atom '())]
    (if (and (seq moves) (> depth 1))
      (do
          (let [aMove (first moves)]
            (debug (str "perftest move-to-make : " aMove) (:coarse debugLevels))
            (debug (str "perftest *** before make-move : " @board) (:fine debugLevels))
            ;; make the move
            (make-move board (:from aMove) (:to aMove))
            (debug (str "perftest *** after make-move : " @board) (:fine debugLevels)))

        ;; make submove is there is another level left
        (let [subMoves (perftest board (dec depth) (get-opposing-color pov))]
          (debug (str "perftest *** before unmake-move : " @board) (:fine debugLevels))
          (unmake-move board)
          (debug (str "perftest *** after unmake-move : " @board) (:fine debugLevels))
          (recur (rest moves)
                 (+ node-count subMoves)
                 local-moves)))
      (+ node-count (count moves)))))
