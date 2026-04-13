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
(ns nl.altindag.client.service.AlephHttpClientService
  (:gen-class)
  (:require [aleph.http :as http]
            [byte-streams :as bs])
  (:import
    [nl.altindag.client.service RequestService]
    [nl.altindag.client.model ClientResponse]
    [nl.altindag.client ClientType Constants]
    [nl.altindag.ssl SSLFactory]
    [nl.altindag.ssl.netty.util NettySslUtils]
    [io.netty.handler.ssl SslContextBuilder SslProvider]))

(defn reify-request-service
  [^SSLFactory ssl-factory]
  (let [netty-ssl-context (-> (NettySslUtils/forClient ssl-factory) (.build))
        pool (http/connection-pool {:connection-options {:ssl-context netty-ssl-context}})]
    (reify
     RequestService
     (executeRequest [this url]
       (let [response @(http/get url {:pool    pool
                                      :headers {Constants/HEADER_KEY_CLIENT_TYPE (.getValue (.getClientType this))}})]
         (ClientResponse. (bs/to-string (:body response)) (:status response))))
     (getClientType [this]
       ClientType/ALEPH_HTTP_CLIENT))))
