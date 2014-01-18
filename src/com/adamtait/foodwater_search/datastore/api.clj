(ns com.adamtait.foodwater-search.datastore.api
  (:require [taoensso.carmine :refer [wcar] :as carmine]))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; ## redis configuration

(def redis-connection {:pool {} :spec {:host "127.0.0.1" :port 6379}})

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; ## redis key generators

(def redis-key-prefix "com.adamtait.foodwater-search:")

(def redis-keys
  {:request-token (fn []
                    (str redis-key-prefix "request-token"))
   :blog-post (fn [number]
                (str redis-key-prefix "blog-post:" number))})


;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; ## request token

(defn request-token-load []
  (wcar redis-connection
        (carmine/get ((:request-token redis-keys)))))

(defn request-token-save
  [request-token]
  (wcar redis-connection
        (carmine/set ((:request-token redis-keys))
                     request-token)))


;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; ## blog posts

(defn blog-posts-load
  [number]
  (wcar redis-connection
        (carmine/get ((:blog-post redis-keys) number))))

(defn blog-posts-save
  [blog-posts]
  (println "blog posts /" blog-posts "/")
  (wcar redis-connection
        carmine/set ((:blog-post redis-keys) blog-posts)))