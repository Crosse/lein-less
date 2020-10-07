(defproject brsyuksel/lein-less "1.7.5"
  :description "Less CSS compiler plugin for leiningen"
  :url "http://github.com/brsyuksel/lein-less"
  :license {:name "Eclipse Public License"
            :url  "http://www.eclipse.org/legal/epl-v10.html"}

  :java-source-paths ["java"]

  :eval-in :leiningen
  :min-lein-version "2.3.0"

  :plugins [[lein-cljfmt "0.7.0"]]

  :profiles {:dev {:dependencies [[org.clojure/clojure "1.6.0" :optional true]
                                  [leiningen-core "2.5.1" :optional true]]}}
  )
