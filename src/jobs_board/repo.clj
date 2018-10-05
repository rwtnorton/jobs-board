(ns jobs-board.repo
  (:require [clojure.set :as set]))

(defprotocol Repo
  (get-all [this])
  (create! [this record])
  (delete! [this id]))

(def required-keys #{:company :title :description})

(defn has-required-keys?
  [m]
  (set/subset? required-keys (set (keys m))))


(defrecord InMemJobsRepo [records]
  Repo
  (get-all [this]
    @records)
  (create! [this record]
    (let [id (str (java.util.UUID/randomUUID))
          r  (select-keys record [:company :title :description])]
      (swap! records assoc id (assoc r :id id))))
  (delete! [this id]
    (when-not (get @records id)
      (throw (ex-info "No such job" {:id id})))
    (swap! records dissoc id)))

(defn new-in-mem-jobs-repo
  ([records]
   {:pre [(map? records)]}
   (map->InMemJobsRepo {:records (atom records)}))
  ([]
   (new-in-mem-jobs-repo {})))

