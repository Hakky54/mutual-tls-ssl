package nl.altindag.client.stepdefs;

import static nl.altindag.client.TestConstants.HTTPS_URL;
import static nl.altindag.client.TestConstants.HTTP_URL;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.atLeast;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Set;

import org.assertj.core.api.Condition;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import ch.qos.logback.classic.Level;
import nl.altindag.client.ClientException;
import nl.altindag.client.TestScenario;
import nl.altindag.client.model.ClientResponse;
import nl.altindag.client.service.ApacheHttpClientWrapper;
import nl.altindag.client.service.GoogleHttpClientWrapper;
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

    private static final Set<String> URLS = Set.of(HTTP_URL, HTTPS_URL);
    private static final Condition<String> HTTP_OR_HTTPS_SERVER_URL = new Condition<>(URLS::contains, "Validates if url is equal to the http or https url of the server");

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
    private GoogleHttpClientWrapper googleHttpClientWrapper;
    @Mock
    private TestScenario testScenario;

    @Test
    public void serverIsAlive() {
        victim.serverIsAlive();

        List<String> logs = super.getLogs(Level.DEBUG);

        assertThat(logs).hasSize(1);
        assertThat(logs).containsExactly("Assuming the server is up and running");
    }

    @Test
    public void iSayHelloWithClientApacheHttpClient() throws Exception {
        ArgumentCaptor<String> urlArgumentCaptor = ArgumentCaptor.forClass(String.class);

        when(apacheHttpClientWrapper.executeRequest(anyString())).thenReturn(mock(ClientResponse.class));

        victim.iSayHelloWithClient("Apache HttpClient");

        verify(apacheHttpClientWrapper, atLeast(1)).executeRequest(urlArgumentCaptor.capture());
        assertThat(urlArgumentCaptor.getValue()).is(HTTP_OR_HTTPS_SERVER_URL);
    }

    @Test
    public void iSayHelloWithClientJdkHttpClient() throws Exception {
        ArgumentCaptor<String> urlArgumentCaptor = ArgumentCaptor.forClass(String.class);

        when(jdkHttpClientWrapper.executeRequest(anyString())).thenReturn(mock(ClientResponse.class));

        victim.iSayHelloWithClient("JDK HttpClient");

        verify(jdkHttpClientWrapper, atLeast(1)).executeRequest(urlArgumentCaptor.capture());
        assertThat(urlArgumentCaptor.getValue()).is(HTTP_OR_HTTPS_SERVER_URL);
    }

    @Test
    public void iSayHelloWithClientOldJdkHttpClient() throws Exception {
        ArgumentCaptor<String> urlArgumentCaptor = ArgumentCaptor.forClass(String.class);

        when(oldJdkHttpClientWrapper.executeRequest(anyString())).thenReturn(mock(ClientResponse.class));

        victim.iSayHelloWithClient("Old JDK HttpClient");

        verify(oldJdkHttpClientWrapper, atLeast(1)).executeRequest(urlArgumentCaptor.capture());
        assertThat(urlArgumentCaptor.getValue()).is(HTTP_OR_HTTPS_SERVER_URL);
    }

    @Test
    public void iSayHelloWithClientSpringRestTemplate() throws Exception {
        ArgumentCaptor<String> urlArgumentCaptor = ArgumentCaptor.forClass(String.class);

        when(springRestTemplateWrapper.executeRequest(anyString())).thenReturn(mock(ClientResponse.class));

        victim.iSayHelloWithClient("Spring RestTemplate");

        verify(springRestTemplateWrapper, atLeast(1)).executeRequest(urlArgumentCaptor.capture());
        assertThat(urlArgumentCaptor.getValue()).is(HTTP_OR_HTTPS_SERVER_URL);
    }

    @Test
    public void iSayHelloWithClientSpringWebFluxWebClientNetty() throws Exception {
        ArgumentCaptor<String> urlArgumentCaptor = ArgumentCaptor.forClass(String.class);

        when(springWebClientNettyWrapper.executeRequest(anyString())).thenReturn(mock(ClientResponse.class));

        victim.iSayHelloWithClient("Spring WebFlux WebClient Netty");

        verify(springWebClientNettyWrapper, atLeast(1)).executeRequest(urlArgumentCaptor.capture());
        assertThat(urlArgumentCaptor.getValue()).is(HTTP_OR_HTTPS_SERVER_URL);
    }

    @Test
    public void iSayHelloWithClientSpringWebFluxWebClientJetty() throws Exception {
        ArgumentCaptor<String> urlArgumentCaptor = ArgumentCaptor.forClass(String.class);

        when(springWebClientJettyWrapper.executeRequest(anyString())).thenReturn(mock(ClientResponse.class));

        victim.iSayHelloWithClient("Spring WebFlux WebClient Jetty");

        verify(springWebClientJettyWrapper, atLeast(1)).executeRequest(urlArgumentCaptor.capture());
        assertThat(urlArgumentCaptor.getValue()).is(HTTP_OR_HTTPS_SERVER_URL);
    }

    @Test
    public void iSayHelloWithClientOkHttp() throws Exception {
        ArgumentCaptor<String> urlArgumentCaptor = ArgumentCaptor.forClass(String.class);

        when(okHttpClientWrapper.executeRequest(anyString())).thenReturn(mock(ClientResponse.class));

        victim.iSayHelloWithClient("OkHttp");

        verify(okHttpClientWrapper, atLeast(1)).executeRequest(urlArgumentCaptor.capture());
        assertThat(urlArgumentCaptor.getValue()).is(HTTP_OR_HTTPS_SERVER_URL);
    }

    @Test
    public void iSayHelloWithClientJerseyClient() throws Exception {
        ArgumentCaptor<String> urlArgumentCaptor = ArgumentCaptor.forClass(String.class);

        when(jerseyClientWrapper.executeRequest(anyString())).thenReturn(mock(ClientResponse.class));

        victim.iSayHelloWithClient("Jersey Client");

        verify(jerseyClientWrapper, atLeast(1)).executeRequest(urlArgumentCaptor.capture());
        assertThat(urlArgumentCaptor.getValue()).is(HTTP_OR_HTTPS_SERVER_URL);
    }

    @Test
    public void iSayHelloWithClientOldJerseyClient() throws Exception {
        ArgumentCaptor<String> urlArgumentCaptor = ArgumentCaptor.forClass(String.class);

        when(oldJerseyClientWrapper.executeRequest(anyString())).thenReturn(mock(ClientResponse.class));

        victim.iSayHelloWithClient("Old Jersey Client");

        verify(oldJerseyClientWrapper, atLeast(1)).executeRequest(urlArgumentCaptor.capture());
        assertThat(urlArgumentCaptor.getValue()).is(HTTP_OR_HTTPS_SERVER_URL);
    }

    @Test
    public void iSayHelloWithClientGoogleHttpTransport() throws Exception {
        ArgumentCaptor<String> urlArgumentCaptor = ArgumentCaptor.forClass(String.class);

        when(googleHttpClientWrapper.executeRequest(anyString())).thenReturn(mock(ClientResponse.class));

        victim.iSayHelloWithClient("Google HttpClient");

        verify(googleHttpClientWrapper, atLeast(1)).executeRequest(urlArgumentCaptor.capture());
        assertThat(urlArgumentCaptor.getValue()).is(HTTP_OR_HTTPS_SERVER_URL);
    }

    @Test
    public void throwExceptionWhenISayHelloWithClientUnknownClient() {
        assertThatThrownBy(() -> victim.iSayHelloWithClient("some dirty client"))
                .isInstanceOf(ClientException.class)
                .hasMessage("Could not find the provided [some dirty client] client type");
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
