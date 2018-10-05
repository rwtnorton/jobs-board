(ns jobs-board.handler
  (:require [compojure.core :refer :all]
            [compojure.route :as route]
            [ring.middleware.json :refer [wrap-json-response
                                          wrap-json-body]]
            [jobs-board.repo :as repo]
            [cheshire.core :as json]
            [clojure.string :as str]))

(defn wrap-jobs-repo
  [f repo]
  (fn [request]
    (f (assoc request :jobs repo))))

(defn get-jobs
  [{:keys [jobs] :as request}]
  (let [rs (repo/get-all jobs)]
    {:body rs}))

(defn post-job
  [{:keys [body jobs] :as request}]
  (let [s    (slurp body)
        data (json/parse-string s keyword)]
    (if (repo/has-required-keys? data)
      (do
        (repo/create! jobs data)
        {:status 201
         :body   (repo/get-all jobs)})
      {:status 400
       :body   {:error (str "Required attributes: "
                            (str/join ", " (map name repo/required-keys)))}})))

(defroutes app-routes
  (GET "/" [] {:body {:message "Hello World"}})
  (GET "/jobs" [] get-jobs)
  (POST "/jobs" [] post-job)
  (route/not-found "Not Found"))

(def app
  (-> app-routes
      (wrap-jobs-repo (repo/new-in-mem-jobs-repo {:foo {:id :foo}
                                                  :bar {:id :bar}}))
      wrap-json-response
      wrap-json-body))
