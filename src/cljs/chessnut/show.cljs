(ns cljs.chessnut.show
  (:require [reagent.core :as reagent :refer [atom]]))

(defn show [component]
  (reagent/render-component (fn [] [component]) (.-body js/document)))

(declare page-2)

(defn page-1 []
  [:div [:h1 "p1"]
   [:input {:type "button" :value "page 2"
            :on-click #(show page-2)}]])

(defn page-2 []
  [:div [:h1 "p2"]
   [:input {:type "button" :value "page 1"
            :on-click #(show page-1)}]])

