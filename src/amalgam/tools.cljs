(ns amalgam.tools
  (:require [clojure.set :as s]))

(declare structure)

(def canon
  {"UserProfile"       #{:firm_advisors :root :firm_investors}
   "Task"              #{:task_set}
   "Plan"              #{:plan_set}
   "Traitifyassesment" #{:traitifyassessment_set}
   "Errors"            #{:errors}
   "QuizAnswer"        #{:quizanswer_set}
   "SocialAccount"     #{:socialaccount_set}
   "Book"              #{:latest_book}
   "Firm"              #{:firm}
   "Agreement"         #{:agreement_set}
   "Groups"            #{:groups}
   "Waypoint"          #{:waypoint :waypoint_set}
   "Joutney"           #{:journeys}
   "Person"            #{:person_set}
   "MoneyBucket"       #{:moneybucket_set}
   "Role"              #{:role}
   "Answer"            #{:answer}
   "Type"              #{:type}})

(defn wrap [[k v]]
  [k (cond
       (sequential? v)
       ;; in vector to show its sequential
       (->> v (map structure) (reduce merge) (vector))
       (map? v) (structure v)
       true nil)])

(defn structure [xs]
  (->> xs (map wrap) (into {})))

(defn infer-relations [[k v]]
  {:type k :many? (vector? v) :key k})

(defn unwrap [[k v]]
  [k (if (vector? v) (first v) v)])

(defn flat
  "[{:type key
     :keys #{key...}
     :relations #{{:many? bool :type key :key key} ...}}
    ...] "
  ([xs] (flat xs :root))
  ([xs key]
   (loop [acc []
          remaining [[key xs]]]
     (if (empty? remaining)
       acc
       (let [[k v] (first remaining)
             children (filter second v)
             relations (into #{} (map infer-relations children))]
         (recur (conj acc {:type k
                           :keys (set (keys v))
                           :relations relations})
                (concat (rest remaining)
                        (map unwrap children))))))))

(defn merge-structure
  [{type :type k1 :keys r1 :relations} {k2 :keys r2 :relations}]
  {:type type
   :keys (s/union k1 k2)
   :relations (s/union r1 r2)})

(defn combine [xs]
  (->> (set (map :type xs))
       (map (fn [type]
              (->> xs
                   (filter #(= type (:type %)))
                   (reduce merge-structure))))))

(defn retype [canon type]
  (->> canon
       (filter #(get (second %) type))
       (map first)
       first))

(defn retype-structure [canon xs]
  (map (fn [x] (update x :type (partial retype canon))) xs))

(defn retype-relations [canon xs]
  (let [update-relation (fn [y] (update y :type (partial retype canon)))
        relations (fn [ys] (into #{} (map update-relation ys)))]
    (map (fn [x] (update x :relations relations)) xs)))

(defn schema [data canon]
  (->> data
       structure
       flat
       (retype-structure canon)
       (retype-relations canon)
       combine))

(defn similar-keys [xs]
  (loop [acc []
         remaining xs]
    (if (empty? remaining)
      acc
      (let [node (first remaining)
            same #(= (:keys node) (:keys %))]
        (recur (conj acc (set (map :type (filter same remaining))))
               (filter (complement same) remaining))))))

