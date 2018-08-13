(ns helda.storage-test
  (:require [clojure.test :refer :all]
            [helda.storage.core :refer :all]
            ))

(deftest init-test
  (let [repo (helda.storage.core.WorldStorageAtom. (atom {}))]
    (save-changes repo {:a1 1 :b1 2})
    (testing "Save and load"
      (is (= 1 ((load-world repo) :a1)))
      (is (= 2 ((load-world repo) :b1)))
      (is (= nil ((load-world repo) :b2)))
      )
    )
  )

(deftest merge-changes
  (let [repo (helda.storage.core.WorldStorageAtom. (atom {:a1 2 :a2 3}))]
    (save-changes repo {:a1 4 :a5 10})
    (testing "Merge changes"
      (is (= 4 ((load-world repo) :a1)))
      (is (= 3 ((load-world repo) :a2)))
      (is (= 10 ((load-world repo) :a5)))
      (is (= nil ((load-world repo) :b2)))
      )
    )
  )
