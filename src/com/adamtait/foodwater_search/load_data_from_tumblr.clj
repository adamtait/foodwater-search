(ns com.adamtait.foodwater-search.load-data-from-tumblr
  (:require [clj-http.client :as http-client]
            [oauth.client :as oauth-client]
            [ring.util.response :as response]))

;;; Tumblr Foodwater-Search App settings

(def oauth-key "z5OmNZRMxmhxGkkVqkI7G8uDMIiqsrV3W3WZNj7wH1Cu2MYaic")
(def oauth-secret "683cS4CoWRSe7HU7FSAz3pJAJcEDM4LCpkW8sMuO00YXzDzHAB")
(def callback-url "http://199.223.234.98/after-oauth")

;;; Tumblr OAuth endpoints

(def tumblr-oauth-endpoint {:request-uri "http://www.tumblr.com/oauth/request_token"
                            :access-uri "http://www.tumblr.com/oauth/access_token"
                            :authorize-uri "http://www.tumblr.com/oauth/authorize"})

;;; Tumblr API endpoints

(def info-api "http://api.tumblr.com/v2/blog/foodwater.org/info")



;;; OAuth structures

(def oauth-consumer
  (oauth-client/make-consumer oauth-key
                              oauth-secret
                              (:request-uri tumblr-oauth-endpoint)
                              (:access-uri tumblr-oauth-endpoint)
                              (:authorize-uri tumblr-oauth-endpoint)
                              :hmac-sha1))

(defn get-request-token
  (oauth-client/request-token oauth-consumer
                              callback-url))


;;; OAuth Flow

(defn new-token-request
  "the first step in the OAuth handshake. gets a request-token, then redirects user to a Tumblr page to authorize access from the app"
  []
  (let [oauth-token (:oauth_token (get-request-token))
        tumblr-authorize-uri (oauth-client/user-approval-uri oauth-consumer
                                                             oauth-token)]
    (response/redirect tumblr-authorize-uri)))

(defn oauth-callback
  "after user authorizes the app, Tumblr lands here. "
  [params]
  (let [request-token (get-request-token)
        access-token-response (oauth-client/access-token oauth-consumer
                                                         request-token
                                                         (:verifier params))]
    {:status 200
     :body (get-blog-info)}))


;;; Tumblr API calls

(defn get-blog-info
  "request info about the blog from the tumblr api"
  [oauth-token oauth-secret]
  (let [credentials (oauth-client/credentials oauth-consumer
                                              oauth-token
                                              oauth-secret
                                              :GET
                                              info-api)]
    (http-client/get info-api :query-params credentials)))
