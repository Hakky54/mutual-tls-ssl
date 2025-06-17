package nl.altindag.client.service

import groovy.test.GroovyTestCase
import nl.altindag.client.util.MockServerTestHelper
import nl.altindag.client.util.SSLFactoryTestHelper

import static nl.altindag.client.ClientType.GROOVY_JAVA_NET_CLIENT
import static nl.altindag.client.TestConstants.HTTPS_URL
import static nl.altindag.client.TestConstants.HTTP_URL
import static org.assertj.core.api.Assertions.assertThat
import static org.junit.jupiter.api.Assertions.assertThrows
import static org.mockito.Mockito.times
import static org.mockito.Mockito.verify

class GroovyJavaNetServiceShould extends GroovyTestCase {

    void testExecuteRequest() {
        MockServerTestHelper.mockResponseForClient(GROOVY_JAVA_NET_CLIENT)

        def sslFactory = SSLFactoryTestHelper.createBasic()
        def victim = new GroovyJavaNetClientService(sslFactory)

        def clientResponse = victim.executeRequest(HTTP_URL)
        assertThat(clientResponse.statusCode).isEqualTo(200)
        assertThat(clientResponse.responseBody).isEqualTo("Hello")
    }

    void testExecuteRequestWithSslUsesSslProperties() {
        def sslFactory = SSLFactoryTestHelper.createBasic()
        def victim = new GroovyJavaNetClientService(sslFactory)

        assertThrows(ConnectException.class, () -> victim.executeRequest(HTTPS_URL))
        verify(sslFactory, times(1)).sslSocketFactory
        verify(sslFactory, times(1)).hostnameVerifier
    }

}
