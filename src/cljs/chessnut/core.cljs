(ns chessnut.core
  (:require [reagent.core :as reagent :refer [atom]]
            [secretary.core :as secretary :refer-macros [defroute]]
            [goog.events :as events]))

(defn log [str]
  (js/console.log str))

(def debug? (atom true))


(def owner? (atom true))

(def room (atom {:owner "owner-id"
                 :room-id "blaaaaa"
                 :empty {}
                 :questions {:message-id-1 {:message "Hello, this is an insightful question,"
                                            :id "the-guy-who-also-wrote-this"
                                            :upvotes ["guy-that-upd1" "second-guy" "third-guy"]
                                            :downvotes []}
                             :message-id-2 {:message "Hey again, a followup."
                                            :id "the-guy-who-wrote-this"
                                            :upvotes []
                                            :downvotes ["guy-that-downvoted2"]}
                             :message-id-3 {:message "Hey"
                                            :id "the-guy-who-wrote-it"
                                            :upvotes ["guy-that-upvoted"]
                                            :downvotes ["guy-that-downvoted3" "guy-that-downvoted2"]}}}))

(defn toggle-debug []
  (swap! debug? (fn [o] (do (log (str "debug?: " o)) (not o)))))

(defn toggle-owner []
  (swap! owner? (fn [o] (do (log (str "owner?: " o)) (not o)))))

(defn toggle-room []
  (swap! room
         (fn [or]
           (let [empty (:empty or)
                 full (:questions or)]
             (-> or
                 (assoc :empty full)
                 (assoc :questions empty))))))


(defn nav-bar []
  [:nav.navbar.navbar-default {:role "navigation"}
   [:div.container [:h1 "Quand"]]])

(defn frame
  "This should be used for ui that always surrounds the content."
  [content]
  [:div
   [nav-bar]
   [:div.container
    content
    ]
   (when @debug?
     [:div
      [:p "Debug:"]
      [:pre (doall (str "owner?:" @owner?))]
      [:pre (doall (str "debug?:" @debug?))]
      [:pre (doall (str @room))]])])


(defn show [component]
  (reagent/render-component (fn [] [component]) (.-body js/document)))


(defn no-questions-owner []
  [:div.container
   [:div.well
    [:h2 "Welcome to your room!"]
    [:p "When questions are submitted they will appear here. Share the link to help your audience join."]
    [:p (str "http://catsandmilk.com/r/" (:room-id @room))]
    ;; todo: fix #_[copy-able link]
    ]])

(defn no-questions-audience []
  [:div.container
   [:div.well
    [:h2 "Welcome to quand!"]
    [:p (doall (str "The topic of discussion is the talk about " (:room-id @room) "."))]
    [:p "When questions are submitted they will appear here. Share the link to help your audience join."]]])

(defn question-panel [{:keys [message id upvotes downvotes] :as question}]
  (let [score (- (count upvotes) (count downvotes))
        o @owner?]
    [:div.question.panel
     [:h2.col-xs-2 score]
     [:div.col-sx-8 [:p message]]
     [:div.button-wrapper.col-xs-2
      (if o
        [:div
         [:button.btn.btn-default.glyphicon.remove.glyphicon-remove]]
        [:div
         [:button.up.btn.btn-success.glyphicon.glyphicon-arrow-up]
         [:button.down.btn.btn-danger.glyphicon.glyphicon-arrow-down]])]]))

(defn display-questions []
  (let [questions (-> @room :questions vals)]
    (if (zero? (count questions))
      (if @owner?
        (no-questions-owner)
        (no-questions-audience))
      [:div (doall (map question-panel questions))])))


(defn landing-page []
  [:div.jumbotron
   [:h2 "Welcome to Quand."]
   [:p "Quand is an easy way to ask and organize questions for your event"]
   [:form
    {:method "GET", :action "/create"}
    [:div.form-group
     [:label "Name your room:"]
     [:input {:type "text", :placeholder "pick a name", :name "title"}]]
    [:input.make-room.btn.btn-default {:value "Create Room"
                                       :type "submit"
                                       :on-click #(show display-questions)}]]])



(defn main []
  (reagent/render-component (fn [] [frame [landing-page]])
                            (.-body js/document)))

(comment
  (toggle-owner)
  (toggle-room)
  (toggle-debug))
