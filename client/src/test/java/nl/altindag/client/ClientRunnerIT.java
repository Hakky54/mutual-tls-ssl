package nl.altindag.client;

import org.junit.runner.RunWith;

import io.cucumber.junit.Cucumber;
import io.cucumber.junit.CucumberOptions;

@RunWith(Cucumber.class)
@CucumberOptions(plugin = { "pretty", "json:target/test-report/report.json" },
                 features = "src/test/resources/features",
                 glue = "nl.altindag.client.stepdefs")
public class ClientRunnerIT {

}
