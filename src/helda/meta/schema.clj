(ns helda.meta.schema
  (:require [schema.core :as s])
  (:require [schema.core :refer [defschema => optional-key maybe eq if]])
  )

(defschema Message{
    :tag s/Keyword
    (optional-key :dst) s/Keyword
    (optional-key :src) s/Keyword
    s/Keyword s/Any
  })

(defschema World{
    s/Keyword s/Any
  })

(defschema Response{
    :response Message
    (optional-key :world) World
  })

(defschema Field{
    :name s/Keyword
    (optional-key :description) s/Str
    :default-value s/Any
    (optional-key :schema) s/Any
    ;second parameter is schema (if provided)
    (optional-key :validator) (=> (maybe Message) [Response])
  })

(defschema Handler{
    :tag s/Keyword
    (optional-key :description) s/Str
    (optional-key :input-msg) {
      s/Keyword s/Str
    }
    (optional-key :examples) [Message]
    :handler (=> Response [Message World])
    (optional-key :msg-schema) s/Any; Input msg schema
    (optional-key :coerce) (=> Message [Message s/Any]);second arg is schema (msg-schema)
    (optional-key :validator) (=> (maybe Message) [Message])
  })

(defschema WorldFixture{
  :tag s/Keyword
  (optional-key :description) s/Str
  :field-values {
    s/Keyword s/Any
    }
  })

(defschema Meta{
  :name s/Keyword
  (optional-key :description) s/Str
  :fields {s/Keyword Field}
  :handlers {s/Keyword Handler}
  :sys-handlers {s/Keyword (=> Message [Message Meta])}
  :fixtures {s/Keyword WorldFixture}
  })
