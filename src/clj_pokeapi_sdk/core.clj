(ns clj-pokeapi-sdk.core
  (:require [clojure.string :as string]
            [martian.core :as martian]
            [martian.clj-http :as martian-http]
            [clojure.tools.logging :as logging]
            [schema.core :as schema]))


(def disable-throwing-exceptions
{:name ::disable-throwing-exceptions
 :enter (fn [ctx]
          (assoc-in ctx [:request :throw-exceptions ] false))})

(def default-api-context
  "Default API context."
  {
   :debug             false
   :martian-http-opts {:interceptors (concat
                                       [
                                        disable-throwing-exceptions
                                        ]
                                       martian-http/default-interceptors)}
   })

(def ^:dynamic *api-context*
  "Dynamic API context to be applied in API calls."
  default-api-context)

(defn set-api-context
  "Set the *api-context* globally"
  [new-context]
  (alter-var-root #'*api-context* (constantly (merge *api-context* new-context))))

(defn create-instance
  "Creates a client."
  ([*api-context* openapi-url]
   (let [{:keys [debug martian-http-opts]} *api-context*]
     (when debug
       (logging/debug openapi-url))
     (martian-http/bootstrap-openapi openapi-url martian-http-opts)))

  ([*api-context* base-url openapi-definition]
   (let [{:keys [debug martian-http-opts]} *api-context*]
     (when debug
       (logging/debug base-url openapi-definition))
     (martian/bootstrap-openapi base-url (martian.file/local-resource openapi-definition) martian-http-opts)))
  )

(defn create-request-params-from-url
  [url]
  (let [[route id] (take-last 2 (string/split url #"/"))]
    {:route (keyword (str "get-" route "-by-id")) :params {:id id}}))

(defn handle-response
  "Returns the body of the response if http status is 200, nil otherwise"
  [response]
  (let [status (:status response)]
    (case status
      200 (:body response)
      404 (logging/warn (:body response))
      nil)))

(defn fetch
  "Runs an HTTP call to retrieve the content for a given route.
   Parameters:
    {:route :get-pokemon-by-name :params {:name \"name\"}}
    an instance of the poke client
  "
  [{:keys [route params]} client]
  (-> client
      (martian/response-for route params)
      handle-response
      ))

(defn- fetch-data
  [client payload]
  (if (and (map? payload) (get payload :url))
    (try
      (-> payload
          (get :url)
          (create-request-params-from-url)
          (fetch client))
      (catch Exception e
        (logging/warn (.getMessage e))
        payload))
    payload))

(defn resolve-url
  "Navigates the payload and tries to resolve all URL references. Substitutes the result to the original leaf.
  Result may contain further URLs which are not automatically resolved to avoid unbounded calls."
  [payload client]
  (clojure.walk/postwalk (partial fetch-data client) payload))

(defn get-field
  "Given a response payload and a field name, return the content of the given field and resolves URL references."
  [data key client]
  (-> data key (resolve-url client)))

(defn response-schema
  "Returns the schema of a given route"
  [route client]
  (->
    (martian/explore client route )
    :returns
    (get 200)
    (schema/explain)))