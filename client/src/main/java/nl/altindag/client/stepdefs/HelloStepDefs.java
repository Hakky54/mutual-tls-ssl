package nl.altindag.client.stepdefs;

import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.cucumber.spring.CucumberContextConfiguration;
import nl.altindag.client.ClientException;
import nl.altindag.client.ClientType;
import nl.altindag.client.Constants;
import nl.altindag.client.TestScenario;
import nl.altindag.client.aspect.LogExecutionTime;
import nl.altindag.client.service.RequestService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static nl.altindag.client.Constants.HELLO_ENDPOINT;
import static org.assertj.core.api.Assertions.assertThat;

@CucumberContextConfiguration
public class HelloStepDefs extends BaseStepDefs {

    private static final Logger LOGGER = LogManager.getLogger(HelloStepDefs.class);

    @Autowired
    @SuppressWarnings("SpringJavaAutowiredMembersInspection")
    public HelloStepDefs(TestScenario testScenario, List<RequestService> requestServices) {
        super(testScenario, requestServices);
    }

    @Given("Server is alive")
    public void serverIsAlive() {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Assuming the server is up and running");
        }
    }

    @LogExecutionTime
    @When("I say hello with {string}")
    public void iSayHelloWithClient(String client) {
        String url = Constants.getServerUrl() + HELLO_ENDPOINT;

        var clientType = ClientType.from(client);
        var clientResponse = getRequestService(clientType)
                .map(requestService -> requestService.execute(url))
                .orElseThrow(() -> new ClientException(String.format("Received a not supported [%s] client type", clientType.getValue())));

        testScenario.setClientResponse(clientResponse);
    }

    @Then("I expect to receive status code {int}")
    public void iExpectToReceiveStatusCodeStatusCode(int statusCode) {
        assertThat(testScenario.getClientResponse().getStatusCode()).isEqualTo(statusCode);
    }

    @Then("I expect to receive {string} message")
    public void iExpectToReceiveBody(String body) {
        assertThat(testScenario.getClientResponse().getResponseBody()).isEqualTo(body);
    }

    @And("I display the time it took to get the message")
    public void iDisplayTheTimeItTookToGetTheMessage() {
        LOGGER.info("Executed request within {} milliseconds", testScenario.getExecutionTimeInMilliSeconds());
    }

}
