(ns angular-phonecat-re-frame.core
    (:require [reagent.core :as reagent :refer [atom]]
              [secretary.core :as secretary :include-macros true]
              [accountant.core :as accountant]))

;; -------------------------
;; Views

(defn wrap-as-element-in-form
  [element]
  [:div {:class "row form-group"}
   element])

(defn check-nil-then-predicate
  "Check if the value is nil, then apply the predicate"
  [value predicate]
  (if (nil? value)
    false
    (predicate value))
  )

(defn eight-or-more-characters?
  [word]
  (check-nil-then-predicate word (fn [arg] (> (count arg) 7))))

(defn has-special-character?
  [word]
  (check-nil-then-predicate word (fn [arg] (boolean (first (re-seq #"\W+" arg))))))

(defn has-number?
  [word]
  (check-nil-then-predicate word (fn [arg] (boolean (re-seq #"\d+" arg)))))

(defn password-requirements
  "A list to describe which password requirements have been met so far"
  [password requirements]
  [:div
   [:ul (->> requirements
             (filter (fn [req] (not ((:check-fn req) @password))))
             (doall)
             (map (fn [req] ^{:key req} [:li (:message req)])))]])

(defn input-element
  "An input element which updates its value and on focus parameters on change, blur, and focus"
  [id name type value in-focus]
  [:input {:id id
           :name name
           :class "form-control"
           :type type
           :required ""
           :value @value
           :on-change #(reset! value (-> % .-target .-value))
           ;; Below we change the state of in-focus
           :on-focus #(swap! in-focus not)
           :on-blur #(swap! in-focus not)}])

(defn input-and-prompt
  "Creates an input box and a prompt box that appears above the input when the input comes into focuse"
  [label-value input-name input-type input-element-arg prompt-element required?]
  (let [input-focus (atom false)]
    (fn []
      [:div
       [:label label-value]
       (if @input-focus prompt-element [:div])
       [input-element input-name input-name input-type input-element-arg input-focus]
       (if (and required? (= "" @input-element-arg))
         [:div "Field is required!"]
         [:div])])))

(defn prompt-message
  "A prompt that will animate to help the user with given input"
  [message]
  [:div {:class "my-messages"}
   [:div {:class "prompt message-animation"} [:p message]]])

(defn email-form
  [email-address-atom]
  (input-and-prompt "email"
                    "email"
                    "email"
                    email-address-atom
                    [prompt-message "What's your email?"]
                    true))

(defn name-form [name-atom]
  (input-and-prompt "name"
                    "name"
                    "text"
                    name-atom
                    (prompt-message "What's your name?")
                    true))

(defn password-form
  [password]
  (let [password-type-atom (atom "password")]
    (fn []
      [:div
       [(input-and-prompt "password"
                          "password"
                          @password-type-atom
                          password
                          (prompt-message "What's your password?")
                          true)]
       [password-requirements password [{:message "8 or more characters" :check-fn eight-or-more-characters?}
                                        {:message "At least one special character" :check-fn has-special-character?}
                                        {:message "At least one number" :check-fn has-number?}]]])))

(defn email-prompt
  []
  (prompt-message "What's your email address?"))

(defn home-page []
  (let [email-address (atom nil)
        name (atom nil)
        password (atom nil)]
    (fn []
      [:div {:class "signup-wrapper"}
       [:h2 "Welcome to TestChimp"]
       [:form
        (wrap-as-element-in-form [email-form email-address])
        (wrap-as-element-in-form [name-form name])
        (wrap-as-element-in-form [password-form password])]])))

(defn about-page []
  [:div [:h2 "About angular-phonecat-re-frame"]
   [:div [:a {:href "/"} "go to the home page"]]])

;; -------------------------
;; Routes

(def page (atom #'home-page))

(defn current-page []
  [:div [@page]])

(secretary/defroute "/" []
  (reset! page #'home-page))

(secretary/defroute "/about" []
  (reset! page #'about-page))

;; -------------------------
;; Initialize app

(defn mount-root []
  (reagent/render [current-page] (.getElementById js/document "app")))

(defn init! []
  (accountant/configure-navigation!
    {:nav-handler
     (fn [path]
       (secretary/dispatch! path))
     :path-exists?
     (fn [path]
       (secretary/locate-route path))})
  (accountant/dispatch-current!)
  (mount-root))
