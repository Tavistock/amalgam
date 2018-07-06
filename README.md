# Amalgam
[![Clojars Project](https://img.shields.io/clojars/v/tavistock/amalgam.svg)](https://clojars.org/tavistock/amalgam)

> "How do you identify? how do you i.d.?"
> - 'Let's relate' by Of Montreal

Tools to denormalize, normalize, and combine nested maps.

## Usage

``` clojure
(ns my-ns.core
  (:require [amalgam.core :refer [norm denorm combine]])
  
(def my-map {:id :a 
             :child {:id :b 
                     :child {:id :c 
                             :value 1}}})

(def normalized (norm my-map)) ;; => makes a map with :results and :entities

(def denormalized (denorm (:entities normalized)
                          (:results normalized)
                          []))

;; => {:id a :child (:amalgam.core/link :b)}

(def denormalized (denorm (:entities normalized)
                          (:results normalized)
                          [[:child [:child]]]))

;; => {:id :a :child {:id :b :child {:id :c :value 1}}}
```
