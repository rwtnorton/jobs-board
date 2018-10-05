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

(defn delete-job
  [{:keys [params jobs] :as request}]
  (let [{:keys [id]} params]
    (if-not id
      {:status 400
       :body   {:error "Missing :id param"}}
      (try
        (repo/delete! jobs id)
        {:status 200
         :body   (repo/get-all jobs)}
        (catch clojure.lang.ExceptionInfo ex
          (if (:id (ex-data ex))
            {:status 404
             :body   {:error "No such job"
                      :id    id}}
            (throw ex)))))))

(defroutes app-routes
  (GET "/jobs" [] get-jobs)
  (POST "/jobs" [] post-job)
  (DELETE "/jobs/:id" [id] delete-job)
  (route/not-found "Not Found"))

(def app
  (-> app-routes
      (wrap-jobs-repo (repo/new-in-mem-jobs-repo))
      wrap-json-response
      wrap-json-body))
