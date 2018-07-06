(ns amalgam.core
  (:require [clojure.spec.alpha :as s]
            [medley.core :refer [distinct-by]]
            [clojure.walk :refer [postwalk]]))

(defn combine [a b] (merge-with merge a b))
(defn link [id] {::link id})
(defn is-link? [link] (::link link))
(defn follow [entities link] (get entities (::link link)))

;; spec for paths mini-language
(s/def ::subpath (s/cat :key keyword? :paths ::paths))
(s/def ::paths (s/coll-of (s/or :key keyword? :subpath ::subpath)))

(defn norm
  ([data] (norm data :id))
  ([data id]
   (let [entities (atom {})
         results (postwalk
                  (fn [node]
                    (if-let [nid (id node)]
                      (do
                        (swap! entities combine {nid node})
                        (link nid))
                      node))
                  data)]
     {:entities @entities :results results})))


(defn denorm
  ([entities entity paths] (denorm entities entity paths :id))
  ([entities entity paths id]
   (cond
     (nil? entity)
     nil

     (is-link? entity)
     (recur entities (follow entities entity) paths id)

     (sequential? entity)
     (map #(denorm entities % paths id) entity)

     (empty? paths)
     entity

     true
     (let [path (first paths)
           [key child-paths] (if (sequential? path) path [path []])
           child (denorm entities (get entity key) child-paths id)]
       (denorm entities (assoc entity key child) (rest paths) id)))))
