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

(ns nl.altindag.client.service.ClojureHttpClientService
  (:gen-class)
  (:import
    [nl.altindag.client.service RequestService]
    [nl.altindag.client.model ClientResponse]
    [nl.altindag.client ClientType Constants ClientException]
    [nl.altindag.ssl SSLFactory]
    [java.net URL HttpURLConnection]
    [java.nio.charset StandardCharsets]
    [javax.net.ssl HttpsURLConnection]
    [org.apache.commons.io IOUtils]
    [org.apache.http.client.methods HttpGet]))

(defn reify-request-service
  [^SSLFactory ssl-factory]
  (reify
    RequestService
    (executeRequest [this url]
      (let [connection (if (.contains url "https:")
                         (doto ^HttpsURLConnection (cast HttpsURLConnection (.openConnection (URL. url)))
                           (.setHostnameVerifier (.getHostnameVerifier ssl-factory))
                           (.setSSLSocketFactory (.getSslSocketFactory ssl-factory)))
                         (if (.contains url "http:")
                           (cast HttpURLConnection (.openConnection (URL. url)))
                           (throw (ClientException. "Could not create a http client for one of these reasons: invalid url, security is enable while using an url with http or security is disable while using an url with https"))))]
        (.setRequestMethod connection HttpGet/METHOD_NAME)
        (.setRequestProperty connection Constants/HEADER_KEY_CLIENT_TYPE (.getValue (.getClientType this)))
        (ClientResponse. (IOUtils/toString (.getInputStream connection) StandardCharsets/UTF_8)
                         (.getResponseCode connection))))
    (getClientType [this]
      ClientType/CLOJURE_HTTP_CLIENT)))
