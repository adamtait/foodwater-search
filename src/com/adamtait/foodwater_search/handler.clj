(ns com.adamtait.foodwater-search.handler
  (:use compojure.core)
  (:require [compojure.handler :as handler]
            [compojure.route :as route]
            [com.adamtait.foodwater-search.load-data-from-tumblr :as load-data-from-tumblr]))

(defn pppp [request]
  (println "request /" request "/")
  {:status 200
   :body request})

(defroutes app-routes
  (GET "/" [] "search for ingredients & recipies on http://foodwater.org")
  (GET "/start-oauth" [] (load-data-from-tumblr/new-token-request))
  (POST "/after-oauth" {params :params} (load-data-from-tumblr/oauth-callback params))
  (route/resources "/")
  (route/not-found "Not Found"))

(def app
  (handler/site app-routes))
