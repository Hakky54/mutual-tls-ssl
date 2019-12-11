package nl.altindag.client.stepdefs;

import static java.util.Objects.isNull;
import static nl.altindag.client.Constants.HELLO_ENDPOINT;
import static nl.altindag.client.Constants.SERVER_URL;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import nl.altindag.client.ClientException;
import nl.altindag.client.ClientType;
import nl.altindag.client.TestScenario;
import nl.altindag.client.model.ClientResponse;
import nl.altindag.client.service.RequestService;

public class HelloStepDefs extends BaseStepDefs {

    private static final Logger LOGGER = LogManager.getLogger(HelloStepDefs.class);

    @Autowired
    public HelloStepDefs(TestScenario testScenario, List<RequestService> requestServices) {
        super(testScenario, requestServices);
    }

    @Given("^Server is alive$")
    public void serverIsAlive() {
        LOGGER.debug("Assuming the server is up and running");
    }

    @When("I say hello with (.*)")
    public void iSayHelloWithClient(String client) throws Exception {
        String url = SERVER_URL + HELLO_ENDPOINT;

        ClientType clientType = ClientType.from(client);
        RequestService requestService = getRequestService(clientType);
        if (isNull(requestService)) {
            throw new ClientException(String.format("Received a not supported [%s] client type", clientType.getValue()));
        }
        ClientResponse clientResponse = requestService.executeRequest(url);
        testScenario.setClientResponse(clientResponse);
    }

    @Then("I expect to receive status code (\\d+)")
    public void iExpectToReceiveStatusCodeStatusCode(int statusCode) {
        assertThat(testScenario.getClientResponse().getStatusCode()).isEqualTo(statusCode);
    }

    @Then("I expect to receive (.*) message")
    public void iExpectToReceiveBody(String body) {
        assertThat(testScenario.getClientResponse().getResponseBody()).isEqualTo(body);
    }

}
