(ns clj-pokeapi-sdk.api.stats
  (:require
    [clj-pokeapi-sdk.core :as core]))

(defn by-id
  "Returns a stat based on id, nil if not found"
  [id client]
  (core/fetch {:route :get-stat-by-id :params {:id id}} client))

(defn by-name
  "Returns a stat based on its name, nil if not found"
  [name client]
  (core/fetch {:route :get-stat-by-name :params {:name name}} client))

(defn response-schema
  "Returns the response schema of a stats API call."
  [client]
  (core/response-schema :get-stat-by-id client))

(defn from-pokemon
  "Resolve all the stats of a pokemon and returns the enriched collection.
  If no stats are available an empty collection is returned"
  [pokemon client]
  (->
    pokemon
    :stats
    (->> (map #(get-in % [:stat :name])))
    (->> (map #(by-name client %))))
  )

