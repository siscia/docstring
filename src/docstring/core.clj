(ns docstring.core
  (:require [clojure.tools.reader.reader-types :as t]
            [clojure.tools.reader.edn :as edn]))

(def text
  "Prova prova
   #in> (+ 1 2)
   #out> 3
   #in> (map inc [2 3 4])
   #out> [3 4 5]
   #in> (require '[clojure.string :as s])
   #out> nil
   ciao")

(defn get-input-output [text]
  (let [pbr (t/string-push-back-reader text)]
    (loop [inputs (atom [])
           outputs (atom [])
           reader {'in> (fn [in] (swap! inputs conj in) in)
                   'out> (fn [out] (swap! outputs conj out) (if (nil? out)
                                                              '(quote nil)
                                                              out))}
           next (edn/read {:eof ::end
                           :readers reader
                           :default (fn [tag value] tag)}
                          pbr)]
      (if (= next ::end)
        {:input inputs
         :output outputs}
        (recur inputs outputs reader (edn/read {:eof ::end
                                                :readers reader}
                                               pbr))))))

(defn c [text]
  (let [output (-> (get-input-output text) :output deref)
        input (-> (get-input-output text) :input deref eval)]
    (= input output)))

(def a [])