(ns com.adamtait.foodwater-search.load-data-from-tumblr
  (:require [clojure.data.json :as json]
            [clj-http.client :as http-client]
            [ring.util.response :as response]
            [oauth.client :as oauth-client]
            [com.adamtait.foodwater-search.routes :refer [routes]]
            [com.adamtait.foodwater-search.datastore.api :as datastore]))

;;; Tumblr Foodwater-Search App settings

(def oauth-key "z5OmNZRMxmhxGkkVqkI7G8uDMIiqsrV3W3WZNj7wH1Cu2MYaic")
(def oauth-secret "683cS4CoWRSe7HU7FSAz3pJAJcEDM4LCpkW8sMuO00YXzDzHAB")
(def callback-url "http://199.223.234.98/after-oauth")

;;; Tumblr OAuth endpoints

(def tumblr-oauth-endpoint {:request-uri "http://www.tumblr.com/oauth/request_token"
                            :access-uri "http://www.tumblr.com/oauth/access_token"
                            :authorize-uri "http://www.tumblr.com/oauth/authorize"})

;;; OAuth structures

(def oauth-consumer
  (oauth-client/make-consumer oauth-key
                              oauth-secret
                              (:request-uri tumblr-oauth-endpoint)
                              (:access-uri tumblr-oauth-endpoint)
                              (:authorize-uri tumblr-oauth-endpoint)
                              :hmac-sha1))

(defn- new-request-token []
  (let [request-token (oauth-client/request-token oauth-consumer
                                                  callback-url)]
    (datastore/request-token-save request-token)
    request-token))

(defn- get-request-token []
  (datastore/request-token-load))


;;; Tumblr API endpoints

(def tumblr-api-base "http://api.tumblr.com/v2/blog/foodwater.org/")

(defn- get-tumblr-api-uri [endpoint]
  (str tumblr-api-base
       (name endpoint)))

;;; Tumblr API calls

(defn- get-blog-info
  "request info about the blog from the tumblr api"
  []
  (http-client/get (get-tumblr-api-uri :info)
                   {:query-params {:api_key oauth-key}
                    :throw-exceptions false}))

(defn- get-blog-posts
  "request info about the blog from the tumblr api"
  []
  (http-client/get (get-tumblr-api-uri :posts)
                   {:query-params {:api_key oauth-key}
                    :throw-exceptions false}))

(defn- load-and-save-blog-posts
  [oauth-token oauth-token-secret]
  (let [blog-posts-json-response (get-blog-posts)
        blog-posts (json/read-str blog-posts-json-response)]
    (datastore/blog-posts-save blog-posts)))


;;; OAuth Flow

(defn new-token-request
  "the first step in the OAuth handshake. gets a request-token, then redirects user to a Tumblr page to authorize access from the app"
  []
  (let [oauth-token (:oauth_token (new-request-token))
        tumblr-authorize-uri (oauth-client/user-approval-uri oauth-consumer
                                                             oauth-token)]
    (response/redirect tumblr-authorize-uri)))

(defn oauth-callback
  "after user authorizes the app, Tumblr lands here. "
  [params]
  (let [access-token-response (oauth-client/access-token oauth-consumer
                                                         (get-request-token)
                                                         (:oauth_verifier params))]
    (load-and-save-blog-posts (:oauth_token access-token-response)
                              (:oauth_token_secret access-token-response))
    (response/redirect (:index routes))))
