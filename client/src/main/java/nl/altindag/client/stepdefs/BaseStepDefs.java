package nl.altindag.client.stepdefs;

import org.springframework.beans.factory.annotation.Autowired;

import nl.altindag.client.SpringBootHelper;
import nl.altindag.client.TestScenario;
import nl.altindag.client.service.ApacheHttpClientWrapper;
import nl.altindag.client.service.JdkHttpClientWrapper;
import nl.altindag.client.service.SpringRestTemplateWrapper;

public class BaseStepDefs extends SpringBootHelper {

    protected static final String APACHE_HTTP_CLIENT = "apache httpclient";
    protected static final String JDK_HTTP_CLIENT = "jdk httpclient";
    protected static final String SPRING_REST_TEMPATE = "spring resttemplate";

    @Autowired
    protected ApacheHttpClientWrapper apacheHttpClientWrapper;
    @Autowired
    protected JdkHttpClientWrapper jdkHttpClientWrapper;
    @Autowired
    protected SpringRestTemplateWrapper springRestTemplateWrapper;
    @Autowired
    protected TestScenario testScenario;

}
