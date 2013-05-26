(defproject betfair-aping-sample "0.1.0-SNAPSHOT"
  :description "A sample app showing basic requests to Betfair's API-NG service, you will need to supply your own AppKey and Session key."
  :url "http://github.com/jamiei/betfair-aping-sample"
  :license {:name "BSD-3"
            :url "http://opensource.org/licenses/BSD-3-Clause"}
  :dependencies [[org.clojure/clojure "1.5.1"]
                          [clj-http "0.7.2"]
                          [cheshire "5.0.2"]]
  :main betfair-aping-sample.core)
