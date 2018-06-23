(ns amalgam.core
  (:require [amalgam.data :refer [user]]))

(def schema
  [{:type "UserProfile"
    :relations
    #{{:type "Groups" :many? true :key :groups}
      {:type "Task" :many? true :key :task_set}
      {:type "QuizAnswer" :many? true :key :quizanswer_set}
      {:type "Agreement" :many? true :key :agreement_set}
      {:type "MoneyBucket" :many? true :key :moneybucket_set}
      {:type "Person" :many? true :key :person_set}
      {:type "Traitifyassesment" :many? true :key :traitifyassessment_set}
      {:type "SocialAccount" :many? true :key :socialaccount_set}
      {:type "Book" :many? false :key :latest_book}
      {:type "UserProfile" :many? true :key :firm_advisors}
      {:type "Plan" :many? true :key :plan_set}
      {:type "Firm" :many? false :key :firm}
      {:type "UserProfile" :many? true :key :firm_investors}}}
   {:type "Journey"
    :relations
    #{{:type "Waypoint" :many? true :key :waypoint_set}}}
   {:type "QuizAnswer" :relations #{{:type "Answer" :many? false :key :answer}}}
   {:type "Plan" :relations #{{:type "Errors" :many? true :key :errors}}}
   {:type "Firm" :relations #{{:type "Joutney" :many? true :key :journeys}}}
   {:type "Waypoint" :relations #{{:type "Role" :many? false :key :role}}}
   {:type "Task" :relations #{{:type "Waypoint" :many? false :key :waypoint}}}
   {:type "MoneyBucket" :relations #{{:type "Type" :many? false :key :type}}}
   {:type "Traitifyassesment" :relations #{}}
   {:type "SocialAccount" :relations #{}}
   {:type "Type" :relations #{}}
   {:type "Agreement" :relations #{}}
   {:type "Person" :relations #{}}
   {:type "Book" :relations #{}}
   {:type "Role" :relations #{}}
   {:type "Groups" :relations #{}}
   {:type "Answer" :relations #{}}
   {:type "Errors" :relations #{}}])

[]

(defn foo
  "I don't do a whole lot."
  [x]
  (println x "Hello, World!"))

(defn django-id [{:keys [id pk name]} type]
  [type (or pk id name)])

(defn related [relations data]
  (mapcat
   (fn [{:keys [many? type key]}]
     (if many?
       (map #(vector type %) (get data key))
       [[type (get data key)]]))
   relations))

(defn unwrap [relations data id]
  (reduce
   (fn [data {:keys [many? key type]}]
     (update data key (fn [x]
                        (if many?
                          (map #(id % type) x)
                          (id x type)))))
   data
   relations))

(defn simple-id [data type] [type (:id data)])

(defn normailize
  "id gets matching data and type as args"
  ([schemas data type] (normailize schemas data type simple-id))
  ([schemas data type id]
   (loop [entities {}
          remaining [[type data]]]
     (if (empty? remaining)
       {:results (id data type)
        :entities entities}
       (let [[type data] (first remaining)
             {:keys [relations]} (first (filter #(= (:type %) type) schemas))]
         (recur (assoc entities (id data type) (unwrap relations data id))
                (concat (rest remaining)
                        (related relations data))))))))

#_(normailize [{:type "A" :relations #{{:type "B" :key :b :many? false }}}]
            {:id 1 :name "a" :b {:id 1 :name "b"}}
            "A")
(comment
  (normailize schema user "UserProfile" django-id))
