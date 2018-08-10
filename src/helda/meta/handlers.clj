(ns helda.meta.handlers
  (:require [schema.core :as s])
  (:require [schema.utils :as su])
  (:require [helda.helpers.request :refer [coerce]])
  (:require [helda.meta.schemas :refer :all])
  )

(s/defn ^:always-validate add-handler :- Meta [meta :- Meta handler :- Handler]
  (assoc-in meta [:handlers (handler :tag)] handler)
  )

(s/defn ^:always-validate add-alias :- Meta
  [meta :- Meta source :- s/Keyword alias :- s/Keyword]
  (assoc-in meta [:handlers alias] (get-in meta [:handlers source]))
  )

(defn coerce-msg [msg handler]
  (if (handler :coerce)
    ((handler :coerce) msg (handler :msg-schema))
    (coerce msg (handler :msg-schema))
    )
  )

(defn validate-msg [msg handler]
  (if (handler :validator)
    ((handler :validator) msg)
    msg
    )
  )

(defn validate-results [response meta]
  (loop [fields (vals (meta :fields)) field (first fields)]
    (if field
      (if-let [value (get-in response [:world (field :name)])]
        (if-let [validator (field :validator)]
          (validator value)
          (if-let [schema (field :schema)]
            (s/validate schema value) ;todo we can provide more info here
            )
          )
        )
      )
    (if-let [next-fields (next fields)]
      (recur next-fields (second fields))
      response
      )
    )
  )

(defn handle [msg meta world]
  (if-not (msg :tag) (throw (Exception. "Msg tag should be set")))
  (when (or (not (msg :world)) (= (msg :world) (meta :name)))
    (if-let [handler (get-in meta [:handlers (msg :tag)])]
      (-> msg
        (coerce-msg handler)
        (validate-msg handler)
        ((handler :handler) world)
        (validate-results meta)
        )
      (if-let [sys-handler (get-in meta [:sys-handlers (msg :tag)])]
        (sys-handler msg meta)
        )
      )
    )
  )
