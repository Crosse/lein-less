(ns leiningen.less.engine
  (:require [clojure.java.io :as jio])
  (:import (java.nio.file Path)
           (java.io File Reader)
           (leiningen.less LessError)
           (java.util Map)
           (org.graalvm.polyglot Context)))



(def ^:dynamic ^:private *engine* nil)


(defn create-engine
  []
  (-> (Context/newBuilder (into-array ["js"]))
      (.allowAllAccess true)
      (.build)))


(defn with-engine* [body-fn]
  (let [engine (create-engine)]
    (binding [*engine* engine]
      (body-fn))))

(defmacro with-engine
  "Run the specified body expressions on the provided javascript engine."
  [& body]
  `(with-engine* (fn [] ~@body)))


(defmacro ^:private check-engine []
  `(when-not *engine*
     (throw (IllegalStateException. "eval! must be called from within a `(with-engine ..)` expression."))))


(defn throwable? [e]
  (when (instance? Throwable e)
    e))


(def ^:private get-class
  (memoize (fn [class-name]
             (try (Class/forName class-name)
                  (catch ClassNotFoundException _ nil)))))


(defmacro dynamic-instance?
  "Given a class name, attempts a dynamic lookup of the class, and if found does an instance? test against the object."
  [class-name obj]
  `(some-> (get-class ~class-name) (instance? ~obj)))


(defn error!
  "Conservative error handling for JS eval function:
    * handles error structures passed directly to this function from JS and throws as LessExceptions
    * handles unwrapping Java exceptions that have been wrapped by the JS VM
   Uses dynamic class resolution to avoid explicit dependencies JVM-internal classes.
   "
  [error message]
  (cond
    (not (throwable? error)) (throw (LessError. (str message) nil))

    (instance? LessError error) (throw error)

    (dynamic-instance? "org.graalvm.polyglot.PolyglotException" error) (throw (LessError. (str message) error))

    :default (throw error)))

(defn eval!
  ([js-expression]
   (check-engine)
   (try
     (.eval *engine* "js" js-expression)
     (catch Exception ex
       (error! ex (.getMessage ex)))))
  ([resource & params]
   (let [content (slurp resource)]
     (eval! content))))
