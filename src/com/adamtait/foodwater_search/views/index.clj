(ns com.adamtait.foodwater-search.views.index
  (:require [net.cgrand.enlive-html :as html]))


(html/deftemplate index "public/html/index.html"
  [{:keys [title]}]
  [:div#title] (html/content title))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; ## bring it all together!

(defn render []
  (index {:title "Foodwater Search"}))
