(ns clj-pokeapi-sdk.api.natures
  (:require
    [clj-pokeapi-sdk.core :as core]))

(defn by-id
  "Returns a nature based on id, nil if not found"
  [id client]
  (core/fetch {:route :get-nature-by-id :params {:id id}} client))

(defn by-name
  "Returns a nature based on name , nil if not found"
  [name client]
  (core/fetch {:route :get-nature-by-name :params {:name name}} client))

(defn response-schema
  "Returns the response schema of a nature API call."
  [client]
  (core/response-schema :get-nature-by-id client))

