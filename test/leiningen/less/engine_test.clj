(ns leiningen.less.engine-test
  (:require [clojure.test :refer :all]
            [leiningen.less.engine :refer :all])
  (:import (leiningen.less LessError)))


(deftest test-default-engine
  (with-engine (create-engine)
    (let [result (-> "'a'+1" eval! .asString)]
      (is (= "a1" result))))
  (is (thrown? IllegalStateException (eval! "+1"))))


(deftest test-engine
  (with-engine
    (is (= (eval! "+1")))
    (eval! "var x = 1")
    (is (= 1 (-> "x" eval! .asInt)))
    (is (= 2 (-> "Java.type('clojure.lang.RT').var('clojure.core','inc').invoke(1)" eval! .asInt)))))



(deftest test-errors
  (with-engine
    (is (thrown-with-msg? LessError #"^Error: Oops" (eval! "throw Error('Oops')")))
    (is (thrown-with-msg? LessError #"^Oops$"
                          (eval! "var error = Java.type('clojure.lang.RT')['var']('leiningen.less.engine', 'error!');
                            error['invoke'](null, 'Oops');")))))


(defmacro tests []
  `(vals (ns-interns ~*ns*)))


(defn test-ns-hook
  "Filter tests based on the type of JVM used to run the tests."
  []
  (let [jvm (System/getProperty "java.version")
        [_ M m] (clojure.string/split jvm #"\.")
        rhino? (#{"5" "6" "7"} M)
        nashorn? (#{"8" "9"} M)
        tests (filter (comp :test meta) (tests))
        tests (concat
                (remove (comp :rhino meta) (remove (comp :nashorn meta) tests))
                (when rhino? (filter (comp :rhino meta) tests))
                (when nashorn? (filter (comp :nashorn meta) tests)))]
    (doseq [test tests]
      (test))))
