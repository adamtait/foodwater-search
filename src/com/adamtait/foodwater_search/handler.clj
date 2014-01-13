(ns com.adamtait.foodwater-search.handler
  (:use compojure.core)
  (:require [compojure.handler :as handler]
            [compojure.route :as route]
            [com.adamtait.foodwater-search.load-data-from-tumblr :as load-data-from-tumblr]))

(defroutes app-routes
  (GET "/" [] "search for ingredients & recipies on http://foodwater.org")
  (POST "/after-oauth" [] (load-data-from-tumblr/oauth-callback))
  (route/resources "/")
  (route/not-found "Not Found"))

(def app
  (handler/site app-routes))
