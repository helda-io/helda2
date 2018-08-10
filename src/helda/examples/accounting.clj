(ns helda.examples.accounting
  (:require [schema.core :as s])
  (:use [helda.meta.core])
  (:use [helda.helpers.response])

  ; (:require [helda.assembly.core :refer :all])
  )

; Accounting app example
(defn create-meta []
  (-> (init-meta :accounts)
    (add-field {
      :name :account-assets-fixed
      :default-value 0
      :description "Fixed Assets"
      :schema s/Num
      })
    (add-field {
      :name :account-assets-cache
      :default-value 0
      :description "Cache"
      :schema s/Num
      })
    (add-field {
      :name :account-assets-bank
      :default-value 0
      :description "Bank"
      :schema s/Num
      })
    (add-field {
      :name :account-liabilites
      :default-value 0
      :description "Company liabilites"
      :schema s/Num
      })
    (add-field {
      :name :account-owner-equities
      :default-value 0
      :description "Company owner equities"
      :schema s/Num
      })
    (add-field {
      :name :accounting-entry-list
      :default-value []
      :description "List of accounting entries"
      :schema [s/Any]
      })
    (add-fixture {
      :tag :sample1
      :field-values {
        :account-owner-equities -1000
        :account-liabilites -500
        :account-assets-bank 500
        :account-assets-fixed 1000
        :account-assets-cache 0
        }
      })
    (add-handler {
      :tag :get-accounts
      :handler (fn [msg world]
        (-> (init-response :get-accounts-response)
          (reply-field :account-assets-fixed (world :account-assets-fixed))
          (reply-field :account-owner-equities (world :account-owner-equities))
          )
        )
      })
    (add-handler {
      :tag :accounting-entry
      :input-msg {
        :debit "Debit account field name"
        :credit "Credit account field name"
        :amount "Money amount"
        }
      :examples [
        {
          :tag :accounting-entry
          :debit :account-assets-fixed
          :credit :account-owner-equities
          :amount 1000
          }
        ]
      :msg-schema {
        :tag s/Keyword
        :debit s/Keyword
        :credit s/Keyword
        :amount s/Num
        }
      :handler (fn [msg world]
        (let [debit-acct (msg :debit) credit-acct (msg :credit)
          amount (msg :amount)]
          (-> (init-response "Reply")
            (save debit-acct (+ (world debit-acct) amount))
            (save credit-acct (- (world credit-acct) amount))
            (save :accounting-entry-list
              (conj (world :accounting-entry-list) msg)
              )
            )
          )
        )
      })
    )
  )

; (defn run-accounting [adapter]
;   (-> (init-assembly adapter)
;     (add-meta (create-meta))
;     run-assembly
;     )
;   )
