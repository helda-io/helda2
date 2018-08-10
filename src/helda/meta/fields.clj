(ns helda.meta.fields
  (:require [schema.core :as s])
  (:require [helda.meta.schemas :refer :all])
  )

(s/defn ^:always-validate add-field :- Meta [meta :- Meta field :- Field]
  (assoc-in meta [:fields (field :name)] field)
  )

(s/defn ^:always-validate add-fields :- Meta [meta :- Meta fields :- [Field]]
  (loop [fields-list fields res meta]
    (if-not (empty? fields-list)
      (recur
        (pop fields-list)
        (add-field res (peek fields-list))
        )
      res
      )
    )
  )

(s/defn ^:always-validate fields :- [s/Keyword] [meta :- Meta]
  (keys (meta :fields)))
(s/defn ^:always-validate fields-table :- [Field] [meta :- Meta]
  (vals (meta :fields)))
(s/defn ^:always-validate lookup-field :- Field [meta :- Meta field :- s/Str]
  (get-in meta [:fields field]))

(s/defn ^:always-validate seed-world :- World [meta :- Meta]
  (loop [world {} fields (vec (vals (meta :fields)))]
    (if-not (empty? fields)
      (let [field (peek fields)]
        (recur
          (assoc world (field :name) (field :default-value))
          (pop fields)
          )
        )
      world
      )
    )
  )
