package nl.altindag.client.stepdefs;

import static org.assertj.core.api.Assertions.assertThat;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import nl.altindag.client.ClientException;
import nl.altindag.client.model.ClientResponse;

public class HelloStepDefs extends BaseStepDefs {

    private static final String SERVER_URL = "http://localhost:8080";
    private static final String HELLO_ENDPOINT = "/api/hello";

    @Given("^Server is alive$")
    public void serverIsAlive() {
        // Assuming the server is up and running
    }

    @When("I say hello with (.*)")
    public void iSayHelloWithClient(String client) throws Exception {
        String url = SERVER_URL + HELLO_ENDPOINT;
        ClientResponse clientResponse;

        if (APACHE_HTTP_CLIENT.equalsIgnoreCase(client)) {
            clientResponse = apacheHttpClientWrapper.executeRequest(url);
        } else if (JDK_HTTP_CLIENT.equalsIgnoreCase(client)) {
            clientResponse = jdkHttpClientWrapper.executeRequest(url);
        } else if (SPRING_REST_TEMPATE.equalsIgnoreCase(client)) {
            clientResponse = springRestTemplateWrapper.executeRequest(url);
        } else {
            throw new ClientException(String.format("Could not found any %s type of client", client));
        }

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
