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
