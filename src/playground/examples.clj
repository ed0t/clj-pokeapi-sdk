(ns playground.examples
  (:require [clj-pokeapi-sdk.core :refer [resolve-url create-instance *api-context*]]
            [clj-pokeapi-sdk.api.pokemon :as pokemon-api]
            [clj-pokeapi-sdk.api.stats :as stats-api]
            [clojure.pprint]))


(def client (create-instance *api-context* "https://pokeapi.co/api/v2" "../resources/openapi.yaml"))

(defn fetch-pokemon-by-name-and-resolve-stats
  []
  (let [pokemon (pokemon-api/by-name "ivysaur" client)]
    ; print all its stats
    (clojure.pprint/pprint (-> pokemon :stats))
    ; the previous call just prints some basics info but does not resolve each stat resource
    ; to enrich the response, append `resolve-url`
    (clojure.pprint/pprint (-> pokemon :stats (resolve-url client)))
    )
  )

(defn fetch-pokemon-by-name-and-resolve-stats-by-name
  []
  (let [pokemon (pokemon-api/by-name "ivysaur" client)]
    (clojure.pprint/pprint (stats-api/from-pokemon pokemon client))))

(defn fetch-pokemon-by-name-and-select-fields-to-return
  []
  (let [pokemon (pokemon-api/by-name  "ivysaur" client)]
    (clojure.pprint/pprint (-> pokemon (select-keys [:name :stats])))
    (clojure.pprint/pprint (-> pokemon (select-keys [:name :stats]) (resolve-url client)))))

(defn resolve-url-can-be-appended-anywhere
  []
  (let [pokemon (pokemon-api/by-name "ivysaur" client)]
    ; given a pokemon response it walks the data structure resolves the direct children which have a URL reference
    ; it does not resolve URLs recursively by choice.
    (clojure.pprint/pprint (-> pokemon (resolve-url client)))))

(defn resolve-url-does-log-a-warning-when-route-is-missing
  []
  (let [pokemon (pokemon-api/by-name "ivysaur" client)]
    (clojure.pprint/pprint (-> pokemon :abilities (resolve-url client)))))









