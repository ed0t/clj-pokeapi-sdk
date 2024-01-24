(ns clj-pokeapi-sdk.api.pokemon
  (:require
    [clj-pokeapi-sdk.core :as core]))

(defn by-id
  "Return a pokemon, given its id, nil if not found"
  [id client]
  (core/fetch {:route :get-pokemon-by-id :params {:id id}} client))

(defn by-name
  "Return a pokemon, given its name, nil if not found"
  [name client]
  (core/fetch {:route :get-pokemon-by-name :params {:name name}} client))

(defn response-schema
  "Returns the response schema of a pokemon API call."
  [client]
  (core/response-schema :get-pokemon-by-id client))



