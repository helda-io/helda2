(ns helda.handlers-test
  (:require [clojure.test :refer :all]
            [helda.examples.accounting :refer :all]
            [helda.meta.core :refer :all]
            [helda.meta.handlers :as h]
            ))

;todo make a test with real handler contract

(deftest add-handler-test
  (let [meta (add-handler (init-meta :handler-test) {
    :tag :msg-handler1
    :input-msg {:txt "this is text msg"}
    :handler (fn [msg world] 2)
    })]
    (testing "Checking handler"
      (is (= 2 (h/handle {:tag :msg-handler1} meta {:meta meta})))
      )
    )
  )
