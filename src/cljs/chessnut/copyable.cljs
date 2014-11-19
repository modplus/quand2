(ns cljs.chessnut.copyable)

(defn copy-able [link]
  [:object {:classid "clsid:d27cdb6e-ae6d-11cf-96b8-444553540000"
            :width   "110"
            :height  "14"
            :id      "clippy"}]
  [:param {:name "movie" :value "/flash/clippy.swf"}]
  [:param {:name "allowScriptAccess" :value "always"}]
  [:param {:name "quality" :value "high"}]
  [:param {:name "scale" :value "noscale"}]
  [:param {:name "FlashVars" :value (str "text=#{" link "}")}]
  [:param {:name "bgcolor" :value "#{bgcolor}"}]
  [:embed {:src "/flash/clippy.swf"
           :width "110"
           :height "14"
           :name "clippy"
           :quality "high"
           :allowScriptAccess "always"
           :type "application/x-shockwave-flash"
           :pluginspage "http://www.macromedia.com/go/getflashplayer"
           :FlashVars "text=#{" link "}"
           :bgcolor "#FF00FF"}])

