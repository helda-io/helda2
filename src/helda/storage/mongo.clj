(ns helda.storage.mongo
  (:require [helda.storage.core :as st])
  (:require [monger.core :as mg])
  (:require [monger.collection :as mc])
  )

(deftype WorldStorageMongo [db key]
  helda.storage.core.WorldStorageProtocol
  (load-world [this]
    (mc/find-one-as-map db "worlds" { :_id key })
    )
  (save-changes [this changes]
    (mc/update db "worlds" {:_id key} (merge {:_id key} changes) {:upsert true})
    )
  )

(defn connect-mongo [uri key]
  (WorldStorageMongo. ((mg/connect-via-uri uri) :db) key)
  )
