(ns betfair-aping-sample.sportsaping
  (:require [clj-http.client :as client]
                [cheshire.core :as cheshire]))


(def ^{:dynamic true} *authentication* "")
(def ^{:dynamic true} *application* "")

(defmacro with-session
  "Applies the session-id header to requests executed within the body."
  [session-id & body]
  `(binding [*authentication* ~session-id]
  ~@body))

(defmacro with-app-key
  "Applies the app-key header to requests executed within the body."
  [app-key & body]
  `(binding [*application* ~app-key]
  ~@body))

(defmacro with-session-and-app-key
  "Applies the session and app-key headers to requests executed within the body."
  [session-id app-key & body]
  `(with-session ~session-id
                 (with-app-key ~app-key ~@body)))

; msg-id atom - accessed via get-next-msg-id
(def msg-id (atom 1))

(defn- get-next-msg-id []
  (swap! msg-id inc))

(defn- get-auth-headers []
  (let [headers {"X-Authentication" *authentication* "X-Application" *application*}]
    (into {} (filter (fn [v] (not (clojure.string/blank? (str (v 1))))) headers))))

(defn- get-endpoint-from-prefix [method-prefix]
  (let [default_endpoint "https://beta-api.betfair.com/betting/json-rpc"
        endpoints {:SportsAPING "https://beta-api.betfair.com/betting/json-rpc"}
        endpoint ((keyword method-prefix) endpoints)]
        (or endpoint default_endpoint)))

(defn- get-json-rpc-call-body [method params id]
  (cheshire/generate-string {"jsonrpc" "2.0" "method" method "params" params "id" id}))

(defn- do-post-to-api-ng
  "Executes a clj-post request to the endpoint looked up  via method-prefix with the body and headers provided."
  [method-prefix body headers]
      (def options  {:body body
                           :headers headers
                           :socket-timeout 2000  ;; in milliseconds
                           :conn-timeout 2000   ;; in milliseconds
                           :content-type :json
                           :accept :json
                           :as :json
                           ;; You should really use a connection-manager to reuse connections
                           :insecure? true})
      (client/with-middleware (filter #(not (= client/wrap-lower-case-headers %)) client/default-middleware)
        (client/post (str (get-endpoint-from-prefix method-prefix)) options)))

(defn do-json-rpc
  "Calls the method provided with the parameters provided. Looks up the endpoint from the method-prefix
  and gets the auth and app-key headers."
  [method params]
    (let [method-prefix (nth (clojure.string/split method #"/") 0)
          json-rpc-body (get-json-rpc-call-body method params (get-next-msg-id))
          headers (get-auth-headers)]
          (do-post-to-api-ng method-prefix json-rpc-body headers)))


;; Interface defined methods
(defn listCompetitions [filter]
  (do-json-rpc "SportsAPING/v1.0/listCompetitions" {"filter" filter}))

(defn listEventTypes [filter]
  (do-json-rpc "SportsAPING/v1.0/listEventTypes" {"filter" filter}))

(defn listMarketCatalogue [filter maxResults]
  (do-json-rpc "SportsAPING/v1.0/listMarketCatalogue" {"filter" filter "maxResults" maxResults}))

(defn listMarketBook [marketIds]
  (do-json-rpc "SportsAPING/v1.0/listMarketBook" {"marketIds" marketIds}))
