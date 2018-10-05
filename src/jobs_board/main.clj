(ns jobs-board.main
  (:gen-class)
  (:require [ring.adapter.jetty :as jetty]
            [jobs-board.handler :as handler]))

(defn -main
  [& args]
  (println "ohai")
  (jetty/run-jetty handler/app {:port 8080}))
