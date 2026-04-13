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

(ns nl.altindag.client.service.ClojureCijHttpClientService
  (:gen-class)
  (:require [clj-http.client :as http])
  (:import
    [nl.altindag.client.service RequestService]
    [nl.altindag.client.model ClientResponse]
    [nl.altindag.client ClientType Constants]
    [nl.altindag.ssl SSLFactory]
    [nl.altindag.ssl.apache4.util Apache4SslUtils]
    [org.apache.http.impl.client HttpClients]))

(defn reify-request-service
  [^SSLFactory ssl-factory]
  (reify
    RequestService
    (executeRequest [this url]
      (let [http-client (-> (HttpClients/custom)
                            (.setSSLSocketFactory (Apache4SslUtils/toSocketFactory ssl-factory))
                            (.build))
            response (http/get url {:headers          {Constants/HEADER_KEY_CLIENT_TYPE (.getValue (.getClientType this))}
                                    :http-client      http-client
                                    :as               :string
                                    :throw-exceptions false})]
        (ClientResponse. (:body response) (:status response))))
    (getClientType [this]
      ClientType/CLOJURE_CLJ_HTTP_CLIENT)))

