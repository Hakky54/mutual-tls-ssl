;
; Copyright 2018 Thunderberry.
;
; Licensed under the Apache License, Version 2.0 (the "License");
; you may not use this file except in compliance with the License.
; You may obtain a copy of the License at
;
;      https://www.apache.org/licenses/LICENSE-2.0
;
; Unless required by applicable law or agreed to in writing, software
; distributed under the License is distributed on an "AS IS" BASIS,
; WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
; See the License for the specific language governing permissions and
; limitations under the License.
;

(ns nl.altindag.client.service.ClojureJdkHttpClientService
  (:gen-class)
  (:import
    [nl.altindag.client.service RequestService]
    [nl.altindag.client.model ClientResponse]
    [nl.altindag.client ClientType Constants]
    [nl.altindag.ssl SSLFactory]
    [java.net URI]
    [java.net.http HttpClient HttpRequest HttpResponse$BodyHandlers]))

(defn reify-request-service
  [^SSLFactory ssl-factory]
  (let [http-client (-> (HttpClient/newBuilder)
                        (.sslContext (.getSslContext ssl-factory))
                        (.sslParameters (.getSslParameters ssl-factory))
                        (.build))]
    (reify
     RequestService
     (executeRequest [this url]
       (let [request (-> (HttpRequest/newBuilder)
                         (.GET)
                         (.header Constants/HEADER_KEY_CLIENT_TYPE (.getValue (.getClientType this)))
                         (.uri (URI/create url))
                         (.build))
             response (.send http-client request (HttpResponse$BodyHandlers/ofString))]
         (ClientResponse. (.body response) (.statusCode response))))
     (getClientType [this]
       ClientType/CLOJURE_JDK_HTTP_CLIENT))))
