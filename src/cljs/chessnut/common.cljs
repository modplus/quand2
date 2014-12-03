(ns chessnut.common
  (:require [chessnut.core :as chess]
            [cljs-http.client :as client]))

(defn nav-bar []
  [:nav.navbar.navbar-default {:role "navigation"}
   [:ul
    [:div.row [:h1 "Quand"]]]])

(defn frame
  "This should be used for ui that always surrounds the content."
  [content]
  [:div
   [nav-bar]
   [:div.container
    content]
   (when (chess/global-state :debug?)
     [:div
      [:p "Debug:"]
      [:pre (doall (str "owner?:" (chess/global-state :owner?)))]
      [:pre (doall (str "debug?:" (chess/global-state :debug?)))]
      [:pre  (str (chess/global-state :room))]])])

(defn no-questions-owner []
  [:div.container
   [:div.well
    [:h2 "Welcome to your room!"]
    [:p "When questions are submitted they will appear here. Share the link to help your audience join."]
    [:p (str "http://catsandmilk.com/r/" (:room-id (chess/global-state :room)))]
    ;; todo: fix #_[copy-able link]
    ]])

(defn no-questions-audience []
  [:div.container
   [:div.well
    [:h2 "Welcome to quand!"]
    [:p (doall (str "The topic of discussion is the talk about " (:room-id (chess/global-state :room)) "."))]
    [:p "When questions are submitted they will appear here. Share the link to help your audience join."]]])

(defn question-panel [{:keys [message id upvotes downvotes] :as question}]
  (let [score (- (count upvotes) (count downvotes))
        o (chess/global-state :owner?)]
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
  (let [questions (-> (chess/global-state :room) :questions vals)
        rooms (client/get "http://catsandmilk.com/json")]
    (pr-str rooms)
    #_(if (zero? (count questions))
        (if (chess/global-state :owner?)
          (no-questions-owner)
          (no-questions-audience))
        [:div (doall (map question-panel questions))]
        (when (not (chess/global-state :owner?))
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
          :on-click #(prn (str "create room: '" @title "'"))}]]])))

