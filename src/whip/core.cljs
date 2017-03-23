(ns ^:figwheel-always whip.core
  (:require [reagent.core :as reagent]
            [clojure.string :as string]))

(def last-id (atom 3))

(defn get-next-id []
  (swap! last-id inc))

(def app-state
  (reagent/atom 
   {:title "Build Whip"
    :stories {1 {:title "Design a data model for projects and stories"
                 :status "done"}
              2 {:title "Create a story title entry form"}
              3 {:title "Implement a way to finish stories"}}}))

(defn done-handler [story-id]
  (fn [e]
    (swap! app-state assoc-in [:stories story-id :status] "done")))

(defn project-board []
  [:ul
   (for [[key s] (get-in @app-state [:stories])]
     (let [title (:title s)]
       [:li {:key (str "item-" key)}
        (if (= "done" (:status s))
          [:del title]
          [:span title 
           [:button {:on-click (done-handler key)} "done"]])]))])

(def tmp-story-title
  (reagent/atom ""))

(defn update-tmp-story-title [e]
  (let [new-title (-> e .-target .-value)]
    (.log js/console (str "Updating title: " new-title))
    (reset! tmp-story-title new-title)))

(defn create-new-story []
  (when (not (string/blank? @tmp-story-title))
    (let [id (get-next-id)
          title @tmp-story-title]
      (swap! app-state assoc-in [:stories id] {:title title :order id})
      (reset! tmp-story-title ""))))

(defn add-story []
  [:div
   [:input {:type "text"
            :value @tmp-story-title
            :auto-focus true
            :placeholder "Add a story..."
            :on-change update-tmp-story-title
            :on-key-down #(case (.-which %)
                            13 (create-new-story)
                            nil)}]
   [:button {:on-click create-new-story} "Add story"]])

(defn whip-main []
  [:div 
   [:h1
    {:id "stories"
     :class "stories main"
     :style {:font-family "cursive"}}
    "Whip Project Management Tool"]
   [add-story]
   [project-board]])

(reagent/render-component [whip-main]
                          (. js/document (getElementById "app")))
