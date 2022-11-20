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
package nl.altindag.client.stepdefs;

import static java.util.stream.Collectors.toMap;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

import nl.altindag.client.ClientType;
import nl.altindag.client.SpringBootHelper;
import nl.altindag.client.TestScenario;
import nl.altindag.client.service.RequestService;

public class BaseStepDefs extends SpringBootHelper {

    protected TestScenario testScenario;
    private final Map<ClientType, RequestService> requestServices;

    public BaseStepDefs(TestScenario testScenario, List<RequestService> requestServices) {
        this.testScenario = testScenario;
        this.requestServices = requestServices.stream()
                .collect(toMap(RequestService::getClientType, Function.identity()));
    }

    public Optional<RequestService> getRequestService(ClientType clientType) {
        return Optional.ofNullable(requestServices.get(clientType));
    }
}
