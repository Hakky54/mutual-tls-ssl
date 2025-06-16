package nl.altindag.client.service

import groovy.test.GroovyTestCase
import nl.altindag.client.util.MockServerTestHelper
import nl.altindag.client.util.SSLFactoryTestHelper

import static nl.altindag.client.ClientType.HYPER_POET_CLIENT
import static nl.altindag.client.TestConstants.HTTP_URL
import static org.assertj.core.api.Assertions.assertThat
import static org.mockito.Mockito.times
import static org.mockito.Mockito.verify

class HyperPoetServiceShould extends GroovyTestCase {

    void testCreateClient() {
        def clientConfiguration = new HyperPoetClientConfiguration()

        def sslFactory = SSLFactoryTestHelper.createBasic()
        def poet = clientConfiguration.httpPoet(sslFactory)

        assertThat(poet).isNotNull()
        verify(sslFactory, times(1)).sslSocketFactory
    }

    void testExecuteRequest() {
        MockServerTestHelper.mockResponseForClient(HYPER_POET_CLIENT)

        def sslFactory = SSLFactoryTestHelper.createBasic()
        def poet = new HyperPoetClientConfiguration().httpPoet(sslFactory)
        def poetService = new HyperPoetService(poet)

        def clientResponse = poetService.executeRequest(HTTP_URL)
        assertThat(clientResponse.statusCode).isEqualTo(200)
        assertThat(clientResponse.responseBody).isEqualTo("Hello")
    }

}
