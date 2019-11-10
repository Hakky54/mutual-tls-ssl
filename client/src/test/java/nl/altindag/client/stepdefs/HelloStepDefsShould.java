package nl.altindag.client.stepdefs;

import static nl.altindag.client.TestConstants.HTTP_URL;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.spi.ILoggingEvent;
import nl.altindag.client.ClientException;
import nl.altindag.client.TestScenario;
import nl.altindag.client.model.ClientResponse;
import nl.altindag.client.service.ApacheHttpClientWrapper;
import nl.altindag.client.service.JdkHttpClientWrapper;
import nl.altindag.client.service.JerseyClientWrapper;
import nl.altindag.client.service.OkHttpClientWrapper;
import nl.altindag.client.service.OldJdkHttpClientWrapper;
import nl.altindag.client.service.OldJerseyClientWrapper;
import nl.altindag.client.service.SpringRestTemplateWrapper;
import nl.altindag.client.service.SpringWebClientJettyWrapper;
import nl.altindag.client.service.SpringWebClientNettyWrapper;
import nl.altindag.client.util.LogTestHelper;

@RunWith(MockitoJUnitRunner.class)
public class HelloStepDefsShould extends LogTestHelper<HelloStepDefs> {

    @InjectMocks
    private HelloStepDefs victim;
    @Mock
    private ApacheHttpClientWrapper apacheHttpClientWrapper;
    @Mock
    private JdkHttpClientWrapper jdkHttpClientWrapper;
    @Mock
    private OldJdkHttpClientWrapper oldJdkHttpClientWrapper;
    @Mock
    private SpringRestTemplateWrapper springRestTemplateWrapper;
    @Mock
    private SpringWebClientNettyWrapper springWebClientNettyWrapper;
    @Mock
    private SpringWebClientJettyWrapper springWebClientJettyWrapper;
    @Mock
    private OkHttpClientWrapper okHttpClientWrapper;
    @Mock
    private JerseyClientWrapper jerseyClientWrapper;
    @Mock
    private OldJerseyClientWrapper oldJerseyClientWrapper;
    @Mock
    private TestScenario testScenario;

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Test
    public void serverIsAlive() {
        victim.serverIsAlive();

        List<ILoggingEvent> logs = super.getLogs();

        assertThat(logs).hasSize(1);
        assertThat(logs.get(0).getLevel()).isEqualTo(Level.DEBUG);
        assertThat(logs.get(0).getFormattedMessage()).isEqualTo("Assuming the server is up and running");
    }

    @Test
    public void iSayHelloWithClientApacheHttpClient() throws Exception {
        victim.iSayHelloWithClient("Apache HttpClient");

        verify(apacheHttpClientWrapper, times(1)).executeRequest(HTTP_URL);
    }

    @Test
    public void iSayHelloWithClientJdkHttpClient() throws Exception {
        victim.iSayHelloWithClient("JDK HttpClient");

        verify(jdkHttpClientWrapper, times(1)).executeRequest(HTTP_URL);
    }

    @Test
    public void iSayHelloWithClientOldJdkHttpClient() throws Exception {
        victim.iSayHelloWithClient("Old JDK HttpClient");

        verify(oldJdkHttpClientWrapper, times(1)).executeRequest(HTTP_URL);
    }

    @Test
    public void iSayHelloWithClientSpringRestTemplate() throws Exception {
        victim.iSayHelloWithClient("Spring RestTemplate");

        verify(springRestTemplateWrapper, times(1)).executeRequest(HTTP_URL);
    }

    @Test
    public void iSayHelloWithClientSpringWebFluxWebClientNetty() throws Exception {
        victim.iSayHelloWithClient("Spring WebFlux WebClient Netty");

        verify(springWebClientNettyWrapper, times(1)).executeRequest(HTTP_URL);
    }

    @Test
    public void iSayHelloWithClientSpringWebFluxWebClientJetty() throws Exception {
        victim.iSayHelloWithClient("Spring WebFlux WebClient Jetty");

        verify(springWebClientJettyWrapper, times(1)).executeRequest(HTTP_URL);
    }

    @Test
    public void iSayHelloWithClientOkHttp() throws Exception {
        victim.iSayHelloWithClient("OkHttp");

        verify(okHttpClientWrapper, times(1)).executeRequest(HTTP_URL);
    }

    @Test
    public void iSayHelloWithClientJerseyClient() throws Exception {
        victim.iSayHelloWithClient("Jersey Client");

        verify(jerseyClientWrapper, times(1)).executeRequest(HTTP_URL);
    }

    @Test
    public void iSayHelloWithClientOldJerseyClient() throws Exception {
        victim.iSayHelloWithClient("Old Jersey Client");

        verify(oldJerseyClientWrapper, times(1)).executeRequest(HTTP_URL);
    }

    @Test
    public void throwExceptionWhenISayHelloWithClientUnknownClient() throws Exception {
        expectedException.expect(ClientException.class);
        expectedException.expectMessage("Could not found any [some dirty client] type of client");
        victim.iSayHelloWithClient("some dirty client");
    }

    @Test
    public void iExpectToReceiveStatusCode200() {
        ClientResponse clientResponse = new ClientResponse("Hello", 200);
        when(testScenario.getClientResponse()).thenReturn(clientResponse);

        victim.iExpectToReceiveStatusCodeStatusCode(200);

        verify(testScenario, times(1)).getClientResponse();
    }

    @Test
    public void iExpectToReceiveBodyHello() {
        ClientResponse clientResponse = new ClientResponse("Hello", 200);
        when(testScenario.getClientResponse()).thenReturn(clientResponse);

        victim.iExpectToReceiveBody("Hello");

        verify(testScenario, times(1)).getClientResponse();
    }

    @Override
    protected Class<HelloStepDefs> getTargetClass() {
        return HelloStepDefs.class;
    }
}
