(ns com.adamtait.foodwater-search.handler
  (:use compojure.core)
  (:require [compojure.handler :as handler]
            [compojure.route :as route]))

(defroutes app-routes
  (GET "/" [] "search for ingredients & recipies on http://foodwater.org")
  (route/resources "/")
  (route/not-found "Not Found"))

(def app
  (handler/site app-routes))
