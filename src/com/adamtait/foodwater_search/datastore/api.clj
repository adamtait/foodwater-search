(ns com.adamtait.foodwater-search.datastore.api
  (:require [taoensso.carmine :refer [wcar] :as carmine]))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; ## redis configuration

(def redis-connection {:pool {} :spec {:host "127.0.0.1" :port 6379}})

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; ## redis key generators

(def redis-key-prefix "com.adamtait.foodwater-search:")

(def redis-keys
  {:request-token (str redis-key-prefix "request-token")
   :blog-post (str redis-key-prefix "blog-posts")})


;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; ## request token

(defn request-token-load []
  (wcar redis-connection
        (carmine/get (:request-token redis-keys))))

(defn request-token-save
  [request-token]
  (wcar redis-connection
        (carmine/set (:request-token redis-keys)
                     request-token)))


;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; ## blog posts

(defn blog-posts-load
  [timestamp]
  (wcar redis-connection
        (carmine/hget (:blog-post redis-keys) timestamp)))

(defn blog-posts-load-all
  []
  (wcar redis-connection
        (carmine/hgetall (:blog-post redis-keys))))

(defn blog-posts-save
  [blog-posts]
  (doseq [blog-post blog-posts]
    (wcar redis-connection
          (carmine/hset (:blog-post redis-keys)
                        (:timestamp blog-post) blog-post))))
