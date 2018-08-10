(ns helda.helpers.request
  (:require [schema.core :as s])
  (:require [schema.utils :as su])
  (:require [schema.coerce :as coerce])
  (:require [helda.meta.schema :refer :all])
  )

(s/defn coerce :- Message [msg :- Message schema]
  (if schema
    (let [res ((coerce/coercer schema coerce/string-coercion-matcher) msg)]
      (if (su/error? res)
        (throw (Exception. (str "Msg coercion failed: " (su/error-val res))))
        res
        )
      )
    msg
    )
  )

; you don't need validation if you did coercion
(s/defn validate-schema [msg :- Message schema]
  (if schema (s/validate schema msg) msg)
  )
