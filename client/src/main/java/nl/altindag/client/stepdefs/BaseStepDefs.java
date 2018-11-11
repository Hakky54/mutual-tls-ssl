package nl.altindag.client.stepdefs;

import nl.altindag.client.SpringBootHelper;
import nl.altindag.client.TestScenario;
import org.apache.http.client.HttpClient;
import org.springframework.beans.factory.annotation.Autowired;

public class BaseStepDefs extends SpringBootHelper {

    @Autowired
    protected HttpClient httpClient;
    @Autowired
    protected TestScenario testScenario;

}
