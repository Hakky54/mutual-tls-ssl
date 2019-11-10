package nl.altindag.client.stepdefs;

import static nl.altindag.client.Constants.APACHE_HTTP_CLIENT;
import static nl.altindag.client.Constants.JDK_HTTP_CLIENT;
import static nl.altindag.client.Constants.JERSEY_CLIENT;
import static nl.altindag.client.Constants.OK_HTTP;
import static nl.altindag.client.Constants.OLD_JDK_HTTP_CLIENT;
import static nl.altindag.client.Constants.OLD_JERSEY_CLIENT;
import static nl.altindag.client.Constants.SPRING_REST_TEMPATE;
import static nl.altindag.client.Constants.SPRING_WEB_CLIENT_JETTY;
import static nl.altindag.client.Constants.SPRING_WEB_CLIENT_NETTY;
import static org.assertj.core.api.Assertions.assertThat;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import nl.altindag.client.ClientException;
import nl.altindag.client.model.ClientResponse;

public class HelloStepDefs extends BaseStepDefs {

    private static final Logger LOGGER = LogManager.getLogger(HelloStepDefs.class);

    private static final String SERVER_URL = "http://localhost:8080";
    private static final String HELLO_ENDPOINT = "/api/hello";

    @Given("^Server is alive$")
    public void serverIsAlive() {
        LOGGER.debug("Assuming the server is up and running");
    }

    @When("I say hello with (.*)")
    public void iSayHelloWithClient(String client) throws Exception {
        String url = SERVER_URL + HELLO_ENDPOINT;
        ClientResponse clientResponse;

        if (APACHE_HTTP_CLIENT.equalsIgnoreCase(client)) {
            clientResponse = apacheHttpClientWrapper.executeRequest(url);
        } else if (JDK_HTTP_CLIENT.equalsIgnoreCase(client)) {
            clientResponse = jdkHttpClientWrapper.executeRequest(url);
        } else if (OLD_JDK_HTTP_CLIENT.equalsIgnoreCase(client)) {
            clientResponse = oldJdkHttpClientWrapper.executeRequest(url);
        } else if (SPRING_REST_TEMPATE.equalsIgnoreCase(client)) {
            clientResponse = springRestTemplateWrapper.executeRequest(url);
        } else if (SPRING_WEB_CLIENT_NETTY.equalsIgnoreCase(client)) {
            clientResponse = springWebClientNettyWrapper.executeRequest(url);
        } else if (SPRING_WEB_CLIENT_JETTY.equalsIgnoreCase(client)) {
            clientResponse = springWebClientJettyWrapper.executeRequest(url);
        } else if (OK_HTTP.equalsIgnoreCase(client)) {
            clientResponse = okHttpClientWrapper.executeRequest(url);
        } else if (JERSEY_CLIENT.equalsIgnoreCase(client)) {
            clientResponse = jerseyClientWrapper.executeRequest(url);
        } else if (OLD_JERSEY_CLIENT.equalsIgnoreCase(client)) {
            clientResponse = oldJerseyClientWrapper.executeRequest(url);
        } else {
            throw new ClientException(String.format("Could not found any [%s] type of client", client));
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
