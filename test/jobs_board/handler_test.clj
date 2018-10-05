(ns jobs-board.handler-test
  (:require [clojure.test :refer :all]
            [ring.mock.request :as mock]
            [jobs-board.handler :refer :all]))

(deftest test-app
  (testing "/jobs route"
    (let [response (app (mock/request :get "/jobs"))]
      (is (= (:status response) 200))
      (is (= (:body response) "{}"))))

  (testing "not-found route"
    (let [response (app (mock/request :get "/invalid"))]
      (is (= (:status response) 404)))))
