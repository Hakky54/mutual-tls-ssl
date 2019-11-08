package nl.altindag.client.stepdefs;

import org.springframework.beans.factory.annotation.Autowired;

import nl.altindag.client.SpringBootHelper;
import nl.altindag.client.TestScenario;
import nl.altindag.client.service.ApacheHttpClientWrapper;
import nl.altindag.client.service.JdkHttpClientWrapper;
import nl.altindag.client.service.JerseyClientWrapper;
import nl.altindag.client.service.OkHttpClientWrapper;
import nl.altindag.client.service.OldJdkHttpClientWrapper;
import nl.altindag.client.service.OldJerseyClientWrapper;
import nl.altindag.client.service.SpringRestTemplateWrapper;
import nl.altindag.client.service.SpringWebClientJettyWrapper;
import nl.altindag.client.service.SpringWebClientNettyWrapper;

public class BaseStepDefs extends SpringBootHelper {

    @Autowired
    protected ApacheHttpClientWrapper apacheHttpClientWrapper;
    @Autowired
    protected JdkHttpClientWrapper jdkHttpClientWrapper;
    @Autowired
    protected OldJdkHttpClientWrapper oldJdkHttpClientWrapper;
    @Autowired
    protected SpringRestTemplateWrapper springRestTemplateWrapper;
    @Autowired
    protected SpringWebClientNettyWrapper springWebClientNettyWrapper;
    @Autowired
    protected SpringWebClientJettyWrapper springWebClientJettyWrapper;
    @Autowired
    protected OkHttpClientWrapper okHttpClientWrapper;
    @Autowired
    protected JerseyClientWrapper jerseyClientWrapper;
    @Autowired
    protected OldJerseyClientWrapper oldJerseyClientWrapper;
    @Autowired
    protected TestScenario testScenario;

}
