(ns chessnut.core
  (:require [reagent.core :as reagent :refer [atom]]
            [secretary.core :as secretary :refer-macros [defroute]]
            [goog.events :as events]
            [chessnut.common :as c]))

(def app-state (atom {}))

;; ----------
;; Helper Functions

(defn global-state [k & [default]]
  (get @app-state k default))

(defn global-put! [k v]
  (swap! app-state assoc k v))

(defn local-put! [a k v]
  (swap! a assoc k v))

;; ----------
;; Routes

(defn app-routes []
  (secretary/set-config! :prefix "#"))

;; ----------
;; Setup

(defn main []
  (reagent/render-component (fn [] [c/frame [c/landing-page]])
                            (.-body js/document)))

(comment
  
  )
