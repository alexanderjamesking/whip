(ns ^:figwheel-always whip.core
  (:require [reagent.core :as reagent]))

(def app-state
  (reagent/atom
   {:projects
    {"aaa"
     {:title "Build Whip"
      :stories {1 {:title "Design a data model for projects and stories"
                   :status "done"
                   :order 1}
                2 {:title "Create a story title entry form"
                   :order 2}
                3 {:title "Implement a way to finish stories"
                   :order 3}}}}}))

(defn done-handler [story-id]
  (fn [e]
    (swap! app-state assoc-in [:projects "aaa" :stories story-id :status] "done")))

(defn project-board [app-state project-id]
                                        ;(.log js/console (get-in @app-state [:projects project-id :stories]))
  [:ul
   (for [[key s] (get-in @app-state [:projects project-id :stories])]
     (let [title (:title s)]
       [:li {:key (str "item-" key)}
        (if (= "done" (:status s))
          [:del title]
          [:span title 
           [:button {:on-click (done-handler key)} "done"]])]))])

(defn whip-main []
  [:div 
   [:h1
    {:id "stories"
     :class "stories main"
     :style {:font-family "cursive"}}
    "Whip Project Management Tool"]
   [project-board app-state "aaa"]
   ])

(reagent/render-component [whip-main]
                          (. js/document (getElementById "app")))
