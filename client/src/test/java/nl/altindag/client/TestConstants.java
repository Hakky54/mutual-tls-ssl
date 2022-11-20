/*
 * Copyright 2018 Thunderberry.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package nl.altindag.client;

public class TestConstants {

    public static final String HTTP_SERVER_URL = "http://localhost:8080";
    public static final String HTTPS_SERVER_URL = "https://localhost:8443";
    public static final String SERVER_HELLO_ENDPOINT = "/api/hello";
    public static final String HTTP_URL = HTTP_SERVER_URL + SERVER_HELLO_ENDPOINT;
    public static final String HTTPS_URL = HTTPS_SERVER_URL + SERVER_HELLO_ENDPOINT;

    public static final String HEADER_KEY_CLIENT_TYPE = "client-type";
    public static final String GET_METHOD = "GET";

    private TestConstants() {}

}
