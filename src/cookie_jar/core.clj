(ns cookie-jar.core
  (:require [clojure.spec.alpha :as s]
            [clojure.spec.test.alpha :as stest]
            [clojure.spec.gen.alpha :as gen]))

(s/def ::cookies nat-int?)
(s/def ::cookie-jar (s/keys :req [::cookies]))

(def actions #{:cookie-monster :grandma :take-one})
(s/def ::action actions)

(s/fdef action
        :args (s/cat :action ::action :state ::cookie-jar)
        :ret ::cookie-jar)

(defmulti action (fn [action state] action))
(defmethod action :cookie-monster [_ _]
  {::cookies 0})
(defmethod action :grandma [_ state]
  (update state ::cookies #(+ % 10)))
(defmethod action :take-one [_ state]
  (update state ::cookies #(if (>= % 1) (- % 1) %)))

(comment (stest/instrument `action))

(s/fdef do-actions
        :args (s/cat :state ::cookie-jar :actions (s/coll-of actions))
        :ret ::cookie-jar)

(defn do-actions [state actions]
  (reduce (fn [s a] (action a s)) state actions))

(comment
  (s/exercise ::cookies)
  (s/exercise ::cookie-jar)

  (def jar #::{:cookies 10})
  (s/valid? ::cookie-jar jar)

  (action :grandma jar)

  (stest/check `action)

  (stest/check `do-actions)

  (do-actions jar [:grandma :take-one :cookie-monster])
  )

