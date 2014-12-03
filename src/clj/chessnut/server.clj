(ns chessnut.server
  (:require [clojure.java.io :as io]
            [clojure.tools.nrepl.server :as nrepl-server]
            [chessnut.dev :refer [is-dev? inject-devmode-html browser-repl start-figwheel]]
            [compojure.core :refer [GET POST defroutes]]
            [compojure.route :refer [resources]]
            [compojure.handler :refer [site]]
            [net.cgrand.enlive-html :refer [deftemplate]]
            [ring.middleware.reload :as reload]
            [environ.core :refer [env]]
            [org.httpkit.server :refer [run-server]]
            [cider.nrepl :refer (cider-nrepl-handler)]))

(deftemplate page
  (io/resource "index.html") [] [:body] (if is-dev? inject-devmode-html identity))

(defroutes routes
  (GET "/" req (page))
  (GET "/test/:hi" [hi] (str "hello, " hi))
  (resources "/")
  (resources "/react" {:root "react"}))

(def http-handler
  (if is-dev?
    (reload/wrap-reload (site #'routes))
    (site routes)))

(defn run [& [port]]
  (defonce ^:private server
    (do
      (if is-dev? (start-figwheel))
      (let [port (Integer. (or port (env :port) 10555))]
        (print "Starting web server on port" port ".\n")
        (run-server http-handler {:port port
                                  :join? false}))))
  server)

(defn -main [& [port]]
  (println (str "Starting nrepl server on " (inc port)))
  (nrepl-server/start-server :port (inc port) :handler cider-nrepl-handler)
  (run port))
