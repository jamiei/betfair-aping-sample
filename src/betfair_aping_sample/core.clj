(ns betfair-aping-sample.core
  (:require [betfair-aping-sample.sportsaping :as sportsaping])
  (:gen-class :main true))


(defn -main [& args]
  (if (< (count args) 2) 
    (println "Arguments error: 2 command line arguments required: session-id application-key")
    (do
      
      (println "Getting the number of soccer markets..")
      (let [resp (sportsaping/with-session-and-app-key (first args) (second args)
                                            (sportsaping/listEventTypes {"eventTypeIds" [1]}))
            {:keys [marketCount]} (first ((resp :body) :result))]
      (println (str "Market Count: " marketCount)))
      
      
      (println "Getting total available for the London Mayoral Election 2014..")
      (let [resp (sportsaping/with-session-and-app-key (first args) (second args)
                                            (sportsaping/listMarketBook ["1.107728324"]))
            {:keys [totalAvailable]} (first ((resp :body) :result))]
      (println (str "Total Available: " (format "%.2f" totalAvailable))))
      
      )))
