(ns jobs-board.handler
  (:require [compojure.core :refer :all]
            [compojure.route :as route]
            [ring.middleware.json :refer [wrap-json-response
                                          wrap-json-body]]))

(defroutes app-routes
  (GET "/" [] {:body {:message "Hello World"}})
  (route/not-found "Not Found"))

(def app
  (-> app-routes
      wrap-json-response
      wrap-json-body))
