package nl.altindag.client;

import org.junit.runner.RunWith;

import cucumber.api.CucumberOptions;
import cucumber.api.junit.Cucumber;

@RunWith(Cucumber.class)
@CucumberOptions(plugin = { "pretty", "json:target/test-report/report.json" },
                 features = "src/test/resources/features",
                 glue = "nl.altindag.client.stepdefs")
public class ClientRunnerIT {

}
