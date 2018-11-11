package nl.altindag.client.stepdefs;

import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.util.EntityUtils;

import java.io.IOException;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;

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

        assertThat(actualStatusCode, equalTo(statusCode));
    }

    @Then("^I expect to receive (.*) message$")
    public void iExpectToReceiveMessage(String message) throws IOException {
        String parsedResponse = EntityUtils.toString(testScenario.getResponse().getEntity());
        assertThat(parsedResponse, equalTo(message));
    }

}
