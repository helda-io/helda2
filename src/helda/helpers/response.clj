(ns helda.helpers.response
  (:require [schema.core :as s])
  (:require [helda.meta.schemas :refer :all])
  )

(s/defn init-response :- Response [tag :- s/Keyword]
  {:response {:tag tag}}
  )

(s/defn reply-msg :- Response [response :- Response msg :- Message]
  (assoc response :response msg)
  )

(s/defn reply-field :- Response
  [
    response :- Response
    key :- s/Keyword
    value :- s/Any
    ]
  (assoc-in response [:response key] value)
  )

(s/defn save :- Response [response :- Response key :- s/Keyword value :- s/Any]
  (assoc-in response [:world key] value)
  )

(s/defn save-changes :- Response [response :- Response changes :- {s/Keyword s/Any}]
  (->> changes
    (merge (response :world))
    (assoc response :world)
    )
  )
