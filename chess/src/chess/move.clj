(ns chess.move
  (:use [clojure.math.numeric-tower])
  (:use chess.bitboard)
  (:use chess.util))

(def ;^{:private true}
  prev-boards
  (atom '()))

;; The moves that have been made
(def ;^{:private true}
  moves-made
  (atom '()))

(defn- push
  [item target]
  (dosync
    (swap! target
             (partial cons item))))
(defn- my-pop
  [target]
  (let [popped (first @target)]
    (dosync
      (swap! target rest))
    popped))

(defn- pushMove
  "Pushes a move onto the moves-made list."
  [aMove move-list]
  (debug (str "pushMove : " aMove) (:fine debugLevels))
  (push aMove move-list))

(defn- popMove
  "Pops a move from the moves-made list."
  [move-list]
  (my-pop move-list))

(defn- create-move
  "Creates a move map."
  ([from to]
   {:from from
    :to   to})
  ([color from fromKey to toKey capture]
   (assert (keyword? fromKey))
   (if toKey
     (assert (keyword? toKey)))
   {:color   color
    :from    from
    :fromKey fromKey 
    :to      to
    :toKey   toKey
    :capture capture}))

(defn- create-move-list
  [from moves]
  (map (fn [x]
         (create-move from x))
       moves))

;; Map of file bitboards
(def ^{:private true}
  files
  {:a [0 8 16 24 32 40 48 56]     ;; A column
   :b [1 9 17 25 33 41 49 57]     ;; B Column
   :g [6 14 22 30 38 46 54 62]    ;; G Column
   :h [7 15 23 31 39 47 55 63]    ;; H column
   :0 [0 1 2 3 4 5 6 7]           ;; 0 row
   :1 [8 9 10 11 12 13 14 15]     ;; 1 row
   :6 [48 49 50 51 52 53 54 55]   ;; 6 row
   :7 [56 57 58 59 60 61 62 63]}) ;; 7 row

(defn- in-file
  "Tests whether a position is in a provided file."
  [file pos]
  (not= (.indexOf (file files) pos) -1))

(defn- valid-pos?
  "Tests whether a position is possible."
  [pos]
  (and (>= pos 0) (< pos 64)))

(defn- test-bit [b bit]
  (.testBit b bit))

(defn- not-occupied?
  "Tests whether a bit is set for a valid position."
  [board pos k]
  (and (valid-pos? pos)
       (not (test-bit (k board) pos))))

(defn- move-made-properly?
  "Test to see whether the move was made correctly."
  [newBoard from fromKey to toKey]
  (and (not-occupied? newBoard from fromKey)
       (if (and toKey (not (= toKey fromKey)))
         (not-occupied? newBoard to toKey) true)
       (not (not-occupied? newBoard to fromKey))))

(defn- make-bigint-clear [b bit]
  (.clearBit b bit))

(defn- make-bigint-set [b bit]
  (.setBit b bit))

(defn- make-bigint-move [old from to]
  (make-bigint-set
    (make-bigint-clear (biginteger old) from)
    to))

(defn get-piece-type
  "Returns a key for the piece type in the position provided."
  [board pos]
  (let [ret (cond
              (test-bit (:nPawn board) pos)   :nPawn
              (test-bit (:nKnight board) pos) :nKnight
              (test-bit (:nBishop board) pos) :nBishop
              (test-bit (:nRook board) pos)   :nRook
              (test-bit (:nQueen board) pos)  :nQueen
              (test-bit (:nKing board) pos)   :nKing)]
    (debug (str "get-piece-type board:" board " pos:" pos " ret:" ret) (:fine debugLevels))
    ret))

(defn- get-piece-color
  "Returns a key for the color in the position provided."
  [board pos]
  (cond
    (test-bit (:nWhite board) pos) :nWhite
    (test-bit (:nBlack board) pos) :nBlack
    :else false))

(defn get-opposing-color
  "Returns the opposite color key of the attacking player."
  ([board pos]
   (cond
     (test-bit (:nWhite board) pos) :nBlack
     (test-bit (:nBlack board) pos) :nWhite
     :else false))
  ([aKey]
   (assert (keyword? aKey))
   (cond
     (= aKey :nWhite) :nBlack
     (= aKey :nBlack) :nWhite
     :else false) ))

;; Ray creating functions
(defn- ray-it 
  "Creates a vector of valid moves starting from pos and in the dir provided."
  [board pos dir]
    (loop [tempRay []
           lastLoc pos
           attack? false]
      (if (and (valid-pos? lastLoc)
               (valid-pos? (+ lastLoc dir))
               (cond 
                 ;(= lastLoc pos) (if (= dir 1)
                                       ;(not (in-file :a (+ lastLoc dir)))
                                       ;true)
                     (= dir 8)       (not (in-file :7 lastLoc))
                     (= dir -8)      (not (in-file :0 lastLoc))
                     (= dir 7)       (not (in-file :a lastLoc))
                     (= dir -7)      (not (in-file :h lastLoc))
                     (= dir -1)      (not (in-file :a lastLoc))
                     (= dir 1)       (not (in-file :h lastLoc))
                     (= dir 9)       (not (in-file :h lastLoc))
                     (= dir -9)      (not (in-file :a lastLoc)))
               ;; Our last move wasn't an attack
               (not attack?)
               ;; Our next move isn't colliding with our own piece
               (not (test-bit ((get-piece-color board pos) board) (+ lastLoc dir)))) 
        (recur (cons (+ lastLoc dir) tempRay)
               (+ lastLoc dir)
               (test-bit ((get-opposing-color board pos) board) (+ lastLoc dir)))
        tempRay)))

(defn- north-west [board pos]
  (ray-it board pos 7))

(defn- north [board pos]
  (ray-it board pos 8))

(defn- north-east [board pos]
  (ray-it board pos 9))

(defn- south-west [board pos]
  (ray-it board pos -7))

(defn- south [board pos]
  (ray-it board pos -8))

(defn- south-east [board pos]
  (ray-it board pos -9))

(defn- east [board pos]
  (ray-it board pos -1))

(defn- west [board pos]
  (ray-it board pos 1))

(defmulti get-move
  "Returns a vector of valid moves (to value) for this piece."
  (fn [board pos] 
    (debug (str "get-move multi pos : " pos " board : " board) (:coarse debugLevels))
    (if (not (keyword? pos)) (get-piece-type @board pos))))

(defmethod get-move :nPawn [board pos]
  (let [oppColor (get-opposing-color @board pos)
        move7    (if (= oppColor :nBlack) (+ pos 7) (- pos 7))
        move8    (if (= oppColor :nBlack) (+ pos 8) (- pos 8))
        move9    (if (= oppColor :nBlack) (+ pos 9) (- pos 9))
        move16   (if (= oppColor :nBlack) (if (and (in-file :1 pos)
                                                   (not (test-bit (oppColor @board) (+ pos 8)))
                                                   (not (test-bit (oppColor @board) (+ pos 16))))
                                            (+ pos 16))
                                          (if (and (in-file :6 pos)
                                                   (not (test-bit (oppColor @board) (- pos 8)))
                                                   (not (test-bit (oppColor @board) (- pos 16))))
                                            (- pos 16)))
        attack7? (test-bit (oppColor @board) move7)
        forward? (not (test-bit (.or (:nWhite @board) (:nBlack @board)) move8))
        attack9? (test-bit (oppColor @board) move9)
        aFile?   (in-file :a pos)
        hFile?   (in-file :h pos)]
    (create-move-list pos 
      (filter integer? (cons move16
        (cond
          (and attack7? forward? attack9? (not aFile?) (not hFile?)) [move7 move8 move9]
          (and attack7? forward? (not aFile?))                       [move7 move8]
          (and forward? attack9? (not hFile?))                       [move8 move9]
          (and attack7? attack9? (not aFile?) (not hFile?))          [move7 move9]
          (and attack7? (not aFile?))                                [move7]
          (and attack9? (not hFile?))                                [move9]
          forward?                                                   [move8]))))))

(defmethod get-move :nKnight [board pos]
  (let [color     (get-piece-color @board pos)
        inABFiles (or (in-file :a pos) (in-file :b pos))
        inGHFiles (or (in-file :g pos) (in-file :h pos))
        in01Files (or (in-file :0 pos) (in-file :1 pos))
        in67Files (or (in-file :6 pos) (in-file :7 pos))
        mFn       (fn [b p]
                    (if (not-occupied? b p color)
                      [p]))
        move6     (if inABFiles nil (mFn @board (+ pos 6)))
        move10    (if inGHFiles nil (mFn @board (+ pos 10)))
        move15    (if in67Files nil (mFn @board (+ pos 15)))
        move17    (if in67Files nil (mFn @board (+ pos 17)))
        n_move6   (if inGHFiles nil (mFn @board (- pos 6)))
        n_move10  (if inABFiles nil (mFn @board (- pos 10)))
        n_move15  (if in01Files nil (mFn @board (- pos 15)))
        n_move17  (if in01Files nil (mFn @board (- pos 17)))]
    (create-move-list pos
      (concat move6 move10 move15 move17 n_move6 n_move10
              n_move15 n_move17))))

(defmethod get-move :nBishop [board pos]
  (let [ray7       (north-east @board pos)
        ray9       (north-west @board pos)
        n_ray7     (south-east @board pos)
        n_ray9     (south-west @board pos)]
    (create-move-list pos
      (concat ray7 ray9 n_ray7 n_ray9))))

(defmethod get-move :nRook [board pos]
  (let [ray1   (west @board pos)
        ray8   (north @board pos)
        n_ray1 (east @board pos)
        n_ray8 (south @board pos)]
    (create-move-list pos
      (concat ray1 ray8 n_ray1 n_ray8))))

(defmethod get-move :nQueen [board pos]
  (let [ray7   (north-west @board pos)
        ray8   (north @board pos)
        ray9   (north-east @board pos)
        ray1   (east @board pos)
        n_ray1 (west @board pos)
        n_ray7 (south-east @board pos)
        n_ray8 (south @board pos)
        n_ray9 (south-west @board pos)]
    (create-move-list pos
      (concat ray7 ray8 ray9 ray1 n_ray1 n_ray7 n_ray8 n_ray9))))

(defmethod get-move :nKing [board pos]
  (let [mFn       (fn [b p]
                    (if (and (not-occupied? b p (get-piece-color b pos)))
                      [p]))
        move1     (if (not (in-file :h pos)) (mFn @board (+ pos 1)))
        move7     (if (not (in-file :a pos)) (mFn @board (+ pos 7)))
        move8     (mFn @board (+ pos 8))
        move9     (if (not (in-file :h pos)) (mFn @board (+ pos 9)))
        n_move1   (if (not (in-file :a pos)) (mFn @board (- pos 1)))
        n_move7   (if (not (in-file :h pos)) (mFn @board (- pos 7)))
        n_move8   (mFn @board (- pos 8))
        n_move9   (if (not (in-file :a pos)) (mFn @board (- pos 9)))]
    (create-move-list pos
      (concat move1 move7 move8 move9 n_move1 n_move7 n_move8 n_move9))))

(defmethod get-move :default [board aKey]
  (debug (str "get-move key : " aKey " board : " board) (:coarse debugLevels))
  (assert (keyword? aKey))
  (loop [oppBits (aKey @board)
         currBit (.getLowestSetBit oppBits)
         moves []]
    (let [nextBits (make-bigint-clear oppBits currBit)]
      ;(debug (str "get-move loop nextBits : " nextBits
                  ;" oppBits : " board " currBit : " currBit)
             ;(:fine debugLevels))

      (if (and (pos? nextBits) (pos? oppBits) (valid-pos? currBit))
          (recur nextBits
                 (.getLowestSetBit nextBits)
                 (concat moves (get-move board currBit)))
        (concat moves (get-move board currBit))))))

(defn in-check?
  "Tests whether a king is in check in a position."
  [board pos]
  (debug (str "in-check? pos : " pos " board : " board) (:fine debugLevels))
  (let [king (.getLowestSetBit (.and ((get-piece-color @board pos) @board)
                                     (:nKing @board)))]
    (not (empty? (filter #(= (:to %) king) (get-move board (get-opposing-color @board pos)))))))

(defn- capture-piece
  "Makes a capture of the specified piece on the provided board."
  [board color pos piece]
  (debug (str "capture-piece color : " color " pos : " pos
              " piece : " piece " board : " board) (:fine debugLevels))
  (assoc board color (make-bigint-clear (color board) pos)
               piece (make-bigint-clear (piece board) pos)))

(defn- move
  "Move the piece on the board."
  [board color from fromKey to toKey capture? move-list]
  (debug (str "move color : " color " from : " from " fromKey : " fromKey
              " to : " to " toKey : " toKey " capture? : " capture?
              " board : " board) (:fine debugLevels))
    (alter board
           (fn [b]
             (let [leBoard (assoc b color (make-bigint-move (color b) from to)
                                    fromKey (make-bigint-move (fromKey b) from to))]
               (debug (str "move isWhite17set? : " (test-bit (:nWhite leBoard) 17)
                           "piece-type : " (get-piece-type leBoard 17))
                      (:coarse debugLevels))
               (if capture?
                 (let [capturedColor (cond
                                       (= color :nWhite) :nBlack
                                       (= color :nBlack) :nWhite)]
                   ;; Push the move onto the stack
                   (pushMove (create-move capturedColor to toKey to nil true) move-list)

                   ;; Capture the piece
                   (if (= fromKey toKey)
                     (capture-piece leBoard capturedColor to capturedColor)
                     (capture-piece leBoard capturedColor to toKey)))
                 
                 ;; No capture, just return the moved piece
                 leBoard)))))

(defn unmake-move
  "Unmakes the last move that was pushed and returns the new board."
  ([board move-list]
    (debug (str "unmake-move board : " @board) (:coarse debugLevels))
    (debug (str "unmake-move move-list : " move-list) (:fine debugLevels))
    (let [lastMove (popMove move-list)]
      (if (seq lastMove)
        (dosync
          (let [capture? (:capture lastMove)
                color    (:color lastMove)
                from     (:from lastMove)
                fromKey  (:fromKey lastMove)
                to       (:to lastMove)
                toKey    (:toKey lastMove)]
            (debug (str "unmake-move lastMove:" lastMove) (:coarse debugLevels))
            (let [unmadeBoard 
                  (if capture?
                    (do
                      ;; Unmake the capturer move
                      (let [nMove (popMove move-list)]
                        (move board (:color nMove)
                                    (:to nMove)
                                    (:fromKey nMove)
                                    (:from nMove)
                                    (:fromKey nMove)
                                    (:capture nMove)
                                    move-list))
                      ;; Unmake the captured piece
                      (move board color from fromKey from toKey false move-list))
                      ;; No capture, just unmake the move
                      (move board color to fromKey from toKey false move-list))]
              (debug (str "unmake-move unmadeBoard:" unmadeBoard) (:coarse debugLevels))
              ;(print-board unmadeBoard)

              ;; If we're in debug mode then let's assert that unmake-move
              ;; unmade the board properly
              (if (> debugLevel 0)
                (do
                  (let [prevBoard (my-pop prev-boards)
                        success?  (is-board-equal @prevBoard unmadeBoard)]
                    (debug (str "unmake-move prevBoard:" @prevBoard " success?:" success?)
                           (:coarse debugLevels))
                    (assert success?))))
              ;; return
              unmadeBoard))))))
  ([board]
   (unmake-move board moves-made)))

(defn make-move 
  "Makes a move on the board and returns the new board."
  ([board from to move-list]
    ;; If we're in debug mode then let's save this board
    ;; so that we can verify during unmake-move
    (if (> debugLevel 0)
      (push board prev-boards))
    (dosync
      (let [colorKey (get-piece-color @board from)
            fromKey  (get-piece-type @board from)
            toKey    (get-piece-type @board to)
            capture? (not (nil? toKey))]
        (debug (str "make-move from: " from " fromKey: " fromKey
                    " to: " to " toKey: " toKey) (:coarse debugLevels))
        (debug (str "make-move move-list: " move-list) (:fine debugLevels))
        ;; Add the move to our move list
        (pushMove (create-move colorKey from fromKey to nil false) move-list)
        ;(if (and (= :nQueen (get-piece-type @board 17))
                 ;(= :nWhite (get-piece-color @board 17)))
          ;(do
            ;(println "making move" move-list)
            ;(print-board @board)))
        ;(if (= to 17)
          ;(do
            ;(print-board @board)
            ;(println "make-move" "ckey" colorKey "from" from "fromkey" fromKey
                 ;"to" to "toKey" toKey "capture" capture?)))

        ;; Make the move
        (let [newBoard (move board colorKey from fromKey to toKey capture? move-list)]
          ;; If we're in debug mode then let's assert that make-move
          ;; made the board properly
          (if (> debugLevel 0)
            (do
              (let [success? (move-made-properly? newBoard from fromKey to toKey)]
                (debug (str "make-move prevBoard:" @board " success?:" success?
                            "\r\nnewBoard:" newBoard) (:coarse debugLevels))
                (assert success?))))
          (debug (str "make-move newBoard:" newBoard) (:coarse debugLevels))
          newBoard))))
  ([board from to]
   (make-move board from to moves-made)))

(defn get-legal-move [board aKey]
  (assert (keyword? aKey))
  (debug (str "get-legal-move key : " aKey " board : " board) (:coarse debugLevels))
  (loop [moves (get-move board aKey)
         legal []]
    (if (seq moves)
      (do
        (make-move board (:from (first moves)) (:to (first moves)))
        (recur (rest moves)
               (let [new-filter (if (in-check? board (:to (first moves)))
                                    legal
                                    (cons (first moves) legal))]
                 (unmake-move board)
                 new-filter)))
      legal)))
