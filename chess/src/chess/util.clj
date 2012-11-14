(ns chess.util)

(def
  debugLevel 1)

(def ^{:private true}
  traces
  [])

(def debugLevels
  {:coarse 1
   :fine   2})

(defn debug
  "A debugging mechanism for the chess program."
  [message level]
  (if (<= level debugLevel) (println message)
    ; else if the message is at a finer log level
    ; but it has been setup as a trace
    (doseq [trace traces]
     (when (re-find (re-pattern (str "^" trace)) message)
        (println message)))))
