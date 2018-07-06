(ns amalgam.core-test
  (:require [cljs.test :refer-macros [deftest is testing are]]
            [amalgam.core :refer [link denorm norm]]))

(def entities
  [{:id 0 :a "a" :b (link 1) :c {:d {:e (link 2)}}}
   {:id 1 :f "f"}
   {:id 2 :g "g"}
   {:id 3 :h [{:i (link 4)} {:i (link 5)} {:i (link 6)}]}
   {:id 4 :j "j"}
   {:id 5 :k "k"}
   {:id 6 :l "l"}])

(def denormer1
  (partial denorm entities {:link0 (link 0)
                            :link1 (link 1)
                            :link3 (link 3)}))

(deftest denorm1-test
  (testing "Denorm can follow links, and list of links"
    (are [x y] (= (denormer1 x) y)
      [:link1]
      {:link0 (link 0)
       :link1 {:id 1 :f "f"}
       :link3 (link 3)}

      [:link0]
      {:link0 {:id 0
               :a "a"
               :b (link 1)
               :c {:d {:e (link 2)}}}
       :link1 (link 1)
       :link3 (link 3)}

      [:link0 :link1]
      {:link0 {:id 0
               :a "a"
               :b (link 1)
               :c {:d {:e (link 2)}}}
       :link1 {:id 1 :f "f"}
       :link3 (link 3)}

      [[:link0 [:b :c]]]
      {:link0 {:id 0
               :a "a"
               :b {:id 1 :f "f"}
               :c {:d {:e (link 2)}}}
       :link1 (link 1)
       :link3 (link 3)}

      [[:link0 [:b [:c [[:d [:e]]]]]]]
      {:link0 {:id 0
               :a "a"
               :b {:id 1 :f "f"}
               :c {:d {:e {:id 2 :g "g"}}}}
       :link1 (link 1)
       :link3 (link 3)}


      [[:link3 [[:h [:i]]]]]
      {:link0 (link 0)
       :link1 (link 1)
       :link3 {:id 3
               :h (list {:i {:id 4 :j "j"}}
                        {:i {:id 5 :k "k"}}
                        {:i {:id 6 :l "l"}})}})))


(deftest denorm2-test
  (testing  "Denorm works on entities, list of entities, and with blank paths"
    (are [x y z] (= (denorm entities x y) z)
      (link 0)
      []
      {:id 0
       :a "a"
       :b (link 1)
       :c {:d {:e (link 2)}}}

      (link 0)
      [:b]
      {:id 0
       :a "a"
       :b {:id 1 :f "f"}
       :c {:d {:e (link 2)}}}

      [(link 0) (link 1)]
      []
      (list {:id 0
             :a "a"
             :b (link 1)
             :c {:d {:e (link 2)}}}
            {:id 1 :f "f"}))))
