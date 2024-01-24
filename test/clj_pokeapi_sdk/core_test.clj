(ns clj-pokeapi-sdk.core-test
  (:require [clj-pokeapi-sdk.core :as core]
            [clojure.test :refer :all]
            [clj-pokeapi-sdk.core :refer :all]))

(deftest create-request-params-from-url-test
  (testing "Extract id and route from URL"
  (let [{:keys [route params]} (core/create-request-params-from-url "http://www.example.com/test/1/")]
    (is (= :get-test-by-id route))
    (is (= {:id "1"} params)))))
