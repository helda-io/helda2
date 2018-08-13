(ns helda.fields-test
  (:require [clojure.test :refer :all]
            [helda.examples.accounting :refer :all]
            [helda.meta.core :refer :all]
            [helda.meta.fields :refer [seed-world]]
            ))

(deftest add-field-test
  (let [meta (add-field (init-meta :example)
    {:name :name-first-name :default-value "N/A" :description "First name"})]
    (testing "Field was added"
      (is (= :name-first-name (get-in meta [:fields :name-first-name :name])))
      (is (= "N/A" (get-in meta [:fields :name-first-name :default-value])))
      (is (= "First name" (get-in meta [:fields :name-first-name :description])))
      )
    )
  )

(deftest add-fields-test
  (let [meta (add-fields (init-meta :example)
    [{:name :address-city :default-value "N/A" :description "City"}
    {:name :address-street :default-value "N/A" :description "Address"}])]
    (testing "Fields were added"
      (is (= :address-city (get-in meta [:fields :address-city :name])))
      (is (= "N/A" (get-in meta [:fields :address-city :default-value])))
      (is (= "City" (get-in meta [:fields :address-city :description])))
      (is (= :address-street (get-in meta [:fields :address-street :name])))
      (is (= "N/A" (get-in meta [:fields :address-street :default-value])))
      (is (= "Address" (get-in meta [:fields :address-street :description])))
      )
    )
  )

(deftest seed-test
  (let [world (seed-world (create-meta)) meta (create-meta)]
    (testing "The same counts of fields in worlds as in meta"
      (is (=
        (count (meta :fields))
        (count world)
        ))
      )
    (testing "Checking field exist with default value"
      (is (= 0 (world :account-assets-fixed)))
      )
    )
  )
