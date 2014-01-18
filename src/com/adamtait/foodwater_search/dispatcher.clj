(ns com.adamtait.foodwater-search.dispatcher
  (:require [compojure.core :refer [defroutes GET POST ANY]]
            [compojure.handler :as handler]
            [compojure.route :as route]
            [com.adamtait.foodwater-search.routes :refer [routes]]
            [com.adamtait.foodwater-search.views.index :as index]
            [com.adamtait.foodwater-search.load-data-from-tumblr :as load-data-from-tumblr]))

(defroutes app-routes
  (GET (:index routes)
       [] (index/render))
  (GET (:oauth-auth-new routes)
       [] (load-data-from-tumblr/new-token-request))
  (ANY (:oauth-token-new routes)
       {params :params} (load-data-from-tumblr/oauth-callback params))
  (route/resources "/")
  (route/not-found "Not Found"))

(def app
  (handler/site app-routes))
