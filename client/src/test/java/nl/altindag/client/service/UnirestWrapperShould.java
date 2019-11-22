package nl.altindag.client.service;

import static nl.altindag.client.ClientType.UNIREST;
import static nl.altindag.client.TestConstants.HEADER_KEY_CLIENT_TYPE;
import static nl.altindag.client.TestConstants.HTTP_URL;
import static org.apache.commons.lang3.StringUtils.EMPTY;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.message.BasicHeader;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.HttpMethod;

import kong.unirest.Unirest;
import kong.unirest.apache.ApacheClient;
import nl.altindag.client.model.ClientResponse;

@SuppressWarnings("SameParameterValue")
@RunWith(MockitoJUnitRunner.class)
public class UnirestWrapperShould {

    private UnirestWrapper victim;
    private HttpClient httpClient;

    @Before
    public void setUp() {
        victim = new UnirestWrapper();

        httpClient = mock(HttpClient.class);
        Unirest.config()
               .httpClient(config -> ApacheClient.builder(httpClient).apply(config));
    }

    @Test
    public void executeRequest() throws IOException {
        mockUnirest("Hello", 200);

        ArgumentCaptor<HttpRequestBase> httpRequestBaseArgumentCaptor = ArgumentCaptor.forClass(HttpRequestBase.class);
        ClientResponse clientResponse = victim.executeRequest(HTTP_URL);

        assertThat(clientResponse.getStatusCode()).isEqualTo(200);
        assertThat(clientResponse.getResponseBody()).isEqualTo("Hello");

        verify(httpClient, times(1)).execute(httpRequestBaseArgumentCaptor.capture());
        assertThat(httpRequestBaseArgumentCaptor.getValue().getURI().toString()).isEqualTo(HTTP_URL);
        assertThat(httpRequestBaseArgumentCaptor.getValue().getFirstHeader(HEADER_KEY_CLIENT_TYPE).getValue()).isEqualTo(UNIREST.getValue());
        assertThat(httpRequestBaseArgumentCaptor.getValue().getMethod()).isEqualTo(HttpMethod.GET.toString());
    }

    private void mockUnirest(String body, int statusCode) throws IOException {
        HttpResponse httpResponse = mock(HttpResponse.class);
        StatusLine statusLine = mock(StatusLine.class);
        HttpEntity httpEntity = mock(HttpEntity.class);

        when(httpResponse.getStatusLine()).thenReturn(statusLine);
        when(statusLine.getReasonPhrase()).thenReturn(EMPTY);
        when(statusLine.getStatusCode()).thenReturn(statusCode);
        when(httpClient.execute(any())).thenReturn(httpResponse);
        when(httpResponse.getEntity()).thenReturn(httpEntity);
        when(httpEntity.getContent()).thenReturn(new ByteArrayInputStream(body.getBytes()));
        when(httpResponse.getAllHeaders()).thenReturn(new Header[]{new BasicHeader(EMPTY, EMPTY)});
    }

    @After
    public void tearDown() {
        Unirest.shutDown();
    }

}
