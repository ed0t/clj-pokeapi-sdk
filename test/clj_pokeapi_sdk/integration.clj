(ns clj-pokeapi-sdk.integration
  (:require [clojure.test :refer :all]
            [clj-pokeapi-sdk.core :as core-api]
            [clj-pokeapi-sdk.api.pokemon :as pokemon-api]
            [clj-pokeapi-sdk.api.natures :as nature-api]
            [clj-pokeapi-sdk.api.stats :as stats-api]
            [clj-pokeapi-sdk.responses :as responses]
            [martian.test :as martian-test]))

(defn create-instance-with-responses
  [response]
  (->
    (core-api/create-instance core-api/*api-context* "https://pokeapi.co/api/v2" "../resources/openapi.yaml")
    (martian-test/respond-with response)))

(deftest fetch-nature
  (testing "fetching nature by id"
    (let [m (create-instance-with-responses {:get-nature-by-id responses/nature-response})
          nature (nature-api/by-id 1 m)]
      (is (= "hardy" (:name nature)))
      (is (= 1 (:id nature)))
      (is (= 3 (-> nature :move_battle_style_preferences count)))
      ))
  (testing "fetching nature by name"
    (let [m (create-instance-with-responses {:get-nature-by-name responses/nature-response})
          nature (nature-api/by-name "hardy" m)]
      (is (= "hardy" (:name nature)))
      (is (= 1 (:id nature)))
      (is (= 3 (-> nature :move_battle_style_preferences count)))
      )))

(deftest fetch-pokemon
  (testing "fetching pokemon by id"
    (let [m (create-instance-with-responses {:get-pokemon-by-id responses/pokemon-response})
          pokemon (pokemon-api/by-id 1 m)]
      (is (= "bulbasaur" (:name pokemon)))

      ))
  (testing "fetching pokemon by name"
    (let [m (create-instance-with-responses {:get-pokemon-by-name responses/pokemon-response})
          pokemon (pokemon-api/by-name "bulbasaur" m)]
      (is (= 1 (:id pokemon)))
      (is (= 1 (core-api/get-field pokemon :id m)))
      ))
  (testing "fetching pokemon and resolve stats"
    (let [m (create-instance-with-responses {:get-pokemon-by-name responses/pokemon-response :get-stat-by-id responses/stats-response})
          pokemon (pokemon-api/by-name "bulbasaur" m)]
      (is (= 1 (:id pokemon)))
      (is (= 6 (count (:stats pokemon))))
      ; when stats are not resolved each entry has a url field
      (is (-> pokemon :stats (->> (every? (-> #(get-in % [:stat :url]))))))
      ; when stats are resolved the url field is removed and stats details are available
      (is (-> pokemon (core-api/get-field :stats m) (->> (not-every? (-> #(get-in % [:stat :url]))))))
      )))


(deftest fetch-stats
  (testing "fetching stats by id"
    (let [m (create-instance-with-responses {:get-stat-by-id responses/stats-response})
          stat (stats-api/by-id  1 m)]
      (is (= "hp" (:name stat)))
      ))
  (testing "fetching stats by name"
    (let [m (create-instance-with-responses {:get-stat-by-name responses/stats-response})
          stat (stats-api/by-name "hp" m)]
      (is (= 1 (:id stat)))
      )))


