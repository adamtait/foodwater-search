(ns com.adamtait.foodwater-search.load-data-from-tumblr
  (:require [clj-http.client :as http-client]
            [oauth.client :as oauth-client]
            [ring.util.response :as response]))


(def foodwater-callback-url "http://199.223.234.98/after-oauth")
(def foodwater-oauth-key "z5OmNZRMxmhxGkkVqkI7G8uDMIiqsrV3W3WZNj7wH1Cu2MYaic")
(def foodwater-oauth-secret "683cS4CoWRSe7HU7FSAz3pJAJcEDM4LCpkW8sMuO00YXzDzHAB")
(def tumblr-oauth-endpoint {:request-uri "http://www.tumblr.com/oauth/request_token"
                            :access-uri "http://www.tumblr.com/oauth/access_token"
                            :authorize-uri "http://www.tumblr.com/oauth/authorize"})

(def foodwater-oauth-consumer
  (oauth-client/make-consumer foodwater-oauth-key
                              foodwater-oauth-secret
                              (:request-uri tumblr-oauth-endpoint)
                              (:access-uri tumblr-oauth-endpoint)
                              (:authorize-uri tumblr-oauth-endpoint)
                              :hmac-sha1))

(def foodwater-request-token
  (oauth-client/request-token foodwater-oauth-consumer
                              foodwater-callback-url))

(def foodwater-info-api "http://api.tumblr.com/v2/blog/foodwater.org/info")


(defn new-token-request []
  (let [tumblr-authorize-uri (oauth-client/user-approval-uri foodwater-oauth-consumer
                                            (:oauth-token foodwater-request-token))]
    (response/redirect tumblr-authorize-uri)))

(defn oauth-callback [params]
  (println "callback params /" params "/")
  (let [access-token-response (oauth-client/access-token foodwater-oauth-consumer
                                                         foodwater-request-token
                                                         (:verifier params))
        credentials (oauth-client/credentials foodwater-oauth-consumer
                                              (:oauth_token access-token-response)
                                              (:oauth_token_secret access-token-response)
                                              :GET
                                              foodwater-info-api)
        foodwater-blog-info (http-client/get foodwater-info-api :query-params credentials)]
    {:status 200
     :body foodwater-blog-info}))
