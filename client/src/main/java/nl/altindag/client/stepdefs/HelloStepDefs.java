package nl.altindag.client.stepdefs;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;

import org.apache.http.client.methods.HttpGet;
import org.apache.http.util.EntityUtils;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

public class HelloStepDefs extends BaseStepDefs {

    private static final String SERVER_URL = "http://localhost:8080";
    private static final String HELLO_ENDPOINT = "/api/hello";

    @Given("^Server is alive$")
    public void serverIsAlive() {
        // Assuming the server is up and running
    }

    @When("^I say hello$")
    public void iSayHello() throws IOException {
        HttpGet request = new HttpGet(SERVER_URL + HELLO_ENDPOINT);
        testScenario.setResponse(httpClient.execute(request));
    }

    @Then("^I expect to receive status code (\\d+)$")
    public void iExpectToReceiveStatusCode(int statusCode) {
        int actualStatusCode = testScenario.getResponse()
                .getStatusLine()
                .getStatusCode();

        assertThat(actualStatusCode).isEqualTo(statusCode);
    }

    @Then("^I expect to receive (.*) message$")
    public void iExpectToReceiveMessage(String message) throws IOException {
        String parsedResponse = EntityUtils.toString(testScenario.getResponse().getEntity());
        assertThat(parsedResponse).isEqualTo(message);
    }

}
