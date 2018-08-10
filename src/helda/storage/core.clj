(ns helda.storage.core)

(defprotocol WorldStorageProtocol
  "World storage protocol"

  (load-world [this] "Returns world")
  (save-changes [this changes] "Saving changes")
  )

(deftype WorldStorageAtom [atom]
  WorldStorageProtocol
  (load-world [this] @atom)
  (save-changes [this changes] (swap! atom merge changes))
  )
