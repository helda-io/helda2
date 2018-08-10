(ns helda.examples.oms
  (:require [schema.core :as s])
  (:use [helda.meta.core])
  (:use [helda.helpers.response])

  ; (:require [helda.assembly.core :refer :all])
  )

(def amount-tolerance 0.1)

(defn is-buy [order] (-> order :buy-sell (= :buy)))

(defn is-sell [order] (-> order :buy-sell (= :sell)))

(defn is-filled [order] (->> order :amount (>= amount-tolerance)))

(defn stack-key [order] (if (is-buy order) :buy-stack :sell-stack))

(defn opp-stack-key [order] (if (is-buy order) :sell-stack :buy-stack))

(defn match-pair [order1 order2]
  (let [order1-price (order1 :price) order2-price (order2 :price)]
    (cond
      (= order1-price order2-price) order2
      (and (is-buy order1) (< order2-price order1-price)) order2
      (and (is-sell order1) (> order2-price order1-price)) order2
      :else nil
      )
    )
  )

(defn match-stack [order order-stack] ;we are trying only first because list sorted
  (some-> order-stack ; {price [orders]}
    first  ; [price [orders]]
    second ;  [orders]
    first  ; order
    (match-pair order)
    )
  )

(defn withdraw-amount [order amount]
  (->> amount
    (- (order :amount))
    (assoc order :amount)
    )
  )

(defn insert-order [order order-stack]
  (if-not (is-filled order)
    (if-not order-stack
      (sorted-map (order :price) [order])
      (->> order
        (conj (order-stack (order :price)))
        (assoc order-stack (order :price))
        )
      )
    order-stack
    )
  )

(defn make-fill [order1 order2]
  (let [fill-amount (min (order1 :amount) (order2 :amount))]
    {
      :amount fill-amount
      :price (order1 :price)
      :order1 (withdraw-amount order1 fill-amount)
      :order2 (withdraw-amount order2 fill-amount)
      }
    )
  )

(defn add-fill [fill changes]
  (conj (if changes (changes :fills) []) fill)
  )

(defn fill-order [order world changes]
  (if-not (is-filled order)
    (let [opp-stack ((or changes world) (opp-stack-key order))]
      (if-let [order2 (match-stack order opp-stack)]
        (let [fill (make-fill order order2)]
          (recur
            (fill :order1)
            world
            {
              :fills (add-fill fill changes)
              (opp-stack-key order)
                (->> opp-stack
                  (remove #(= order2 %))
                  (insert-order (fill :order2))
                  )
            })
          )
          (assoc changes (stack-key order)
            (insert-order order (-> order stack-key world))
            )
        )
      )
      changes
    )
  )

(defn create-meta []
  (-> (init-meta :accounts)
    (add-field {
      :name :buy-stack
      :default-value nil
      :description "Bid orders stack"
      ; :schema s/Num
      })
    (add-field {
      :name :sell-stack
      :default-value nil
      :description "Ask orders stack"
      ; :schema s/Num
      })
    (add-field {
      :name :fills
      :default-value []
      :description "Order fills"
      ; :schema s/Num
      })
    (add-handler {
      :tag :order
      :input-msg {
        :id "Order Id"
        :sym "Symbol"
        :amount "Money amount"
        :price "Price"
        :cp "Counterparty"
        :buy-sell "Bid/offer side"
        }
      :examples [
        {
          :tag :order
          :id 1
          :amount 100
          :price 12.5
          :cp "CP1"
          :buy-sell :buy
          }
        ]
      :msg-schema {
        :tag s/Keyword
        :id s/Num
        :sym s/Str
        :amount s/Num
        :price s/Num
        :cp s/Str
        :buy-sell (s/enum :buy :sell)
        }
      :handler (fn [msg world]
        (let [fill-response (fill-order msg world nil)]
          (cond-> (init-response :order)
            (fill-response :buy-stack)
              (save :buy-stack (fill-response :buy-stack))
            (fill-response :sell-stack)
              (save :sell-stack (fill-response :sell-stack))
            (fill-response :fills) (save :fills (conj (world :fills) (fill-response :fills)))
            (fill-response :fills) (reply-field :fills (fill-response :fills))
            (not (fill-response :fills)) (reply-field :fills [])
            )
          )
        )
      })
    )
  )

; (defn run-oms [adapter]
;   (-> (init-assembly adapter)
;     (add-meta (create-meta))
;     run-assembly
;     )
;   )
