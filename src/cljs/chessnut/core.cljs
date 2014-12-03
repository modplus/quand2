(ns chessnut.core
  (:require [reagent.core :as reagent :refer [atom]]
            [secretary.core :as secretary :refer-macros [defroute]]
            [goog.events :as events]
            [cljs-http.client :as client]))

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
      [:pre  (str @room)]])])



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
  (let [questions (-> @room :questions vals)
        rooms (client/get "http://catsandmilk.com/json")]
    (pr-str rooms)
    #_(if (zero? (count questions))
        (if @owner?
          (no-questions-owner)
          (no-questions-audience))
        [:div (doall (map question-panel questions))]
        (when (not @owner?)
          [:input {:type "text"}]))))

(defn name-room [title]
  [:p (if (= "" title)
        "Please enter a room name."
        (str "Let's get started with your room: " title))])

(defn landing-page []
  (let [title (atom "")]
    (fn []
      [:div.jumbotron
       [:h2 "Welcome to Quand."]
       [:p "Quand is an easy way to ask and organize questions for your event"]
       [:form
        [:div.form-group
         [name-room @title]
         [:input {:type "text"
                  :name "title"
                  :value @title
                  :on-change #(reset! title (-> % .-target .-value))
                  :placeholder "pick a name"}]]
        [:input.make-room.btn.btn-default
         {:default-value "Create Room"
          :on-click #((post-create-room title)
                      (show display-questions))}]]])))

(defroute "/r/:room" [room]
  (js/alert room))

(defn show [component]
  (reagent/render-component (fn [] [frame [component]]) (.-body js/document)))


(defn main []
  (show landing-page))

(comment

  (show landing-page)

  (toggle-owner)
  (toggle-room)
  (toggle-debug)

  (client/get "/test/apples")
  )
