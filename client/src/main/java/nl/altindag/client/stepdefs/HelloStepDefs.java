package nl.altindag.client.stepdefs;

import static nl.altindag.client.Constants.HELLO_ENDPOINT;
import static nl.altindag.client.Constants.SERVER_URL;
import static org.assertj.core.api.Assertions.assertThat;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import nl.altindag.client.ClientException;
import nl.altindag.client.ClientType;
import nl.altindag.client.model.ClientResponse;

public class HelloStepDefs extends BaseStepDefs {

    private static final Logger LOGGER = LogManager.getLogger(HelloStepDefs.class);

    @Given("^Server is alive$")
    public void serverIsAlive() {
        LOGGER.debug("Assuming the server is up and running");
    }

    @When("I say hello with (.*)")
    public void iSayHelloWithClient(String client) throws Exception {
        String url = SERVER_URL + HELLO_ENDPOINT;
        ClientResponse clientResponse;

        ClientType clientType = ClientType.from(client);
        switch (clientType) {
            case APACHE_HTTP_CLIENT:        { clientResponse = apacheHttpClientWrapper.executeRequest(url); }       break;
            case JDK_HTTP_CLIENT:           { clientResponse = jdkHttpClientWrapper.executeRequest(url); }          break;
            case OLD_JDK_HTTP_CLIENT:       { clientResponse = oldJdkHttpClientWrapper.executeRequest(url); }       break;
            case SPRING_REST_TEMPATE:       { clientResponse = springRestTemplateWrapper.executeRequest(url); }     break;
            case SPRING_WEB_CLIENT_NETTY:   { clientResponse = springWebClientNettyWrapper.executeRequest(url); }   break;
            case SPRING_WEB_CLIENT_JETTY:   { clientResponse = springWebClientJettyWrapper.executeRequest(url); }   break;
            case OK_HTTP:                   { clientResponse = okHttpClientWrapper.executeRequest(url); }           break;
            case JERSEY_CLIENT:             { clientResponse = jerseyClientWrapper.executeRequest(url); }           break;
            case OLD_JERSEY_CLIENT:         { clientResponse = oldJerseyClientWrapper.executeRequest(url); }        break;
            case GOOGLE_HTTP_CLIENT:        { clientResponse = googleHttpClientWrapper.executeRequest(url); }       break;
            case UNIREST:                   { clientResponse = unirestWrapper.executeRequest(url); }                break;
            case RETROFIT:                  { clientResponse = retrofitWrapper.executeRequest(null); }          break;
            default: throw new ClientException(String.format("Received a not supported [%s] client type", clientType.getValue()));
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
