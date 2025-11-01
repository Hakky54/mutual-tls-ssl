/*
 * Copyright 2018 Thunderberry.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package nl.altindag.client;

import com.github.mizosoft.methanol.Methanol;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.gson.GsonBuilder;
import com.sun.jersey.api.client.config.DefaultClientConfig;
import com.sun.jersey.client.urlconnection.HTTPSProperties;
import feign.Feign;
import feign.googlehttpclient.GoogleHttpClient;
import feign.hc5.ApacheHttp5Client;
import feign.http2client.Http2Client;
import feign.httpclient.ApacheHttpClient;
import io.vertx.core.Vertx;
import io.vertx.core.net.KeyCertOptions;
import io.vertx.core.net.TrustOptions;
import io.vertx.ext.web.client.WebClientOptions;
import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import kong.unirest.Unirest;
import nl.altindag.ssl.SSLFactory;
import nl.altindag.ssl.apache4.util.Apache4SslUtils;
import nl.altindag.ssl.apache5.util.Apache5SslUtils;
import nl.altindag.ssl.jetty.util.JettySslUtils;
import nl.altindag.ssl.netty.util.NettySslUtils;
import okhttp3.OkHttpClient;
import org.apache.cxf.bus.CXFBusFactory;
import org.apache.cxf.configuration.jsse.TLSClientParameters;
import org.apache.cxf.jaxrs.client.JAXRSClientFactoryBean;
import org.apache.cxf.transport.http.HTTPConduitConfigurer;
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManagerBuilder;
import org.apache.hc.client5.http.impl.nio.PoolingAsyncClientConnectionManagerBuilder;
import org.apache.http.conn.socket.LayeredConnectionSocketFactory;
import org.apache.http.impl.client.HttpClients;
import org.asynchttpclient.AsyncHttpClient;
import org.asynchttpclient.Dsl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Scope;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.http.client.reactive.JettyClientHttpConnector;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import javax.net.ssl.SSLException;
import java.net.http.HttpClient;

@Component
public class ClientConfig {

    @Bean
    @Scope("prototype")
    public org.apache.http.impl.client.CloseableHttpClient apacheHttpClient(SSLFactory sslFactory) {
        LayeredConnectionSocketFactory socketFactory = Apache4SslUtils.toSocketFactory(sslFactory);
        return HttpClients.custom()
                .setSSLSocketFactory(socketFactory)
                .build();
    }

    @Bean
    public org.apache.http.impl.nio.client.CloseableHttpAsyncClient apacheHttpAsyncClient(SSLFactory sslFactory) {
        var client = org.apache.http.impl.nio.client.HttpAsyncClients.custom()
                .setSSLContext(sslFactory.getSslContext())
                .setSSLHostnameVerifier(sslFactory.getHostnameVerifier())
                .build();
        client.start();
        return client;
    }

    @Bean
    public org.apache.hc.client5.http.impl.classic.CloseableHttpClient apache5HttpClient(SSLFactory sslFactory) {
        var connectionManager = PoolingHttpClientConnectionManagerBuilder.create()
                .setTlsSocketStrategy(Apache5SslUtils.toTlsSocketStrategy(sslFactory))
                .build();

        return org.apache.hc.client5.http.impl.classic.HttpClients.custom()
                .setConnectionManager(connectionManager)
                .build();
    }

    @Bean
    public org.apache.hc.client5.http.impl.async.CloseableHttpAsyncClient apache5HttpAsyncClient(SSLFactory sslFactory) {
        var connectionManager = PoolingAsyncClientConnectionManagerBuilder.create()
                .setTlsStrategy(Apache5SslUtils.toTlsStrategy(sslFactory))
                .build();

        var client = org.apache.hc.client5.http.impl.async.HttpAsyncClients.custom()
                .setConnectionManager(connectionManager)
                .build();

        client.start();
        return client;
    }

    @Bean
    public HttpClient jdkHttpClient(SSLFactory sslFactory) {
        return HttpClient.newBuilder()
                .sslParameters(sslFactory.getSslParameters())
                .sslContext(sslFactory.getSslContext())
                .build();
    }

    @Bean
    public RestTemplate restTemplate(org.apache.hc.client5.http.impl.classic.CloseableHttpClient httpClient) {
        return new RestTemplate(new HttpComponentsClientHttpRequestFactory(httpClient));
    }

    @Bean("okHttpClient")
    @Scope("prototype")
    public OkHttpClient okHttpClient(SSLFactory sslFactory) {
        return new OkHttpClient.Builder()
                .sslSocketFactory(sslFactory.getSslSocketFactory(), sslFactory.getTrustManager().orElseThrow())
                .build();
    }

    @Bean
    @Scope("prototype")
    public reactor.netty.http.client.HttpClient nettyHttpClient(SSLFactory sslFactory) throws SSLException {
        var sslContext = NettySslUtils.forClient(sslFactory).build();
        return reactor.netty.http.client.HttpClient.create()
                .secure(sslSpec -> sslSpec.sslContext(sslContext));
    }

    @Bean
    @Scope("prototype")
    public org.eclipse.jetty.client.HttpClient jettyHttpClient(SSLFactory sslFactory) {
        var sslContextFactory = JettySslUtils.forClient(sslFactory);
        org.eclipse.jetty.client.HttpClient httpClient = new org.eclipse.jetty.client.HttpClient();
        httpClient.setSslContextFactory(sslContextFactory);
        return httpClient;
    }

    @Bean("webClientWithNetty")
    public WebClient webClientWithNetty(reactor.netty.http.client.HttpClient httpClient) {
        return WebClient.builder()
                .clientConnector(new ReactorClientHttpConnector(httpClient))
                .build();
    }

    @Bean("webClientWithJetty")
    public WebClient webClientWithJetty(org.eclipse.jetty.client.HttpClient httpClient) {
        return WebClient.builder()
                .clientConnector(new JettyClientHttpConnector(httpClient))
                .build();
    }

    @Bean
    public Client jerseyClient(SSLFactory sslFactory) {
        return ClientBuilder.newBuilder()
                .sslContext(sslFactory.getSslContext())
                .hostnameVerifier(sslFactory.getHostnameVerifier())
                .build();
    }

    @Bean
    public com.sun.jersey.api.client.Client oldJerseyClient(SSLFactory sslFactory) {
        var clientConfig = new DefaultClientConfig();
        clientConfig.getProperties().put(HTTPSProperties.PROPERTY_HTTPS_PROPERTIES, new HTTPSProperties(sslFactory.getHostnameVerifier(), sslFactory.getSslContext()));
        return com.sun.jersey.api.client.Client.create(clientConfig);
    }

    /**
     * JAX-RS configuration should be identical to {@link #jerseyClient(SSLFactory)} once CXF update to version 3.5.0
     * But this function is still necessary to create CXF version of client that does not depend on Java SPI (/META-INF/services/javax.ws.rs.client.ClientBuilder)
     */
    @Bean("cxf")
    public javax.ws.rs.client.Client cxfJaxRsClient(SSLFactory sslFactory) {
        // One can just use ClientBuilder.newBuilder(), Explicit use here is due to multiple JAX-RS implementations in classpath
        return new org.apache.cxf.jaxrs.client.spec.ClientBuilderImpl()
                .sslContext(sslFactory.getSslContext())
                .hostnameVerifier(sslFactory.getHostnameVerifier())
                .build();
    }

    @Bean
    public org.apache.cxf.jaxrs.client.WebClient cxfWebClient(SSLFactory sslFactory) {
        var factory = new JAXRSClientFactoryBean();
        factory.setAddress(Constants.getServerUrl());
        // One can also get conduit from  WebClient.getConfig(webClient).getHttpConduit() and change it directly
        factory.setBus(new CXFBusFactory().createBus());
        factory.getBus().setExtension((name, address, httpConduit) -> {
            var tls = new TLSClientParameters();
            tls.setSSLSocketFactory(sslFactory.getSslSocketFactory());
            tls.setHostnameVerifier(sslFactory.getHostnameVerifier());
            httpConduit.setTlsClientParameters(tls);
        }, HTTPConduitConfigurer.class);
        return factory.createWebClient();
    }

    @Bean
    public HttpTransport googleHttpClient(SSLFactory sslFactory) {
        return new NetHttpTransport.Builder()
                .setSslSocketFactory(sslFactory.getSslSocketFactory())
                .setHostnameVerifier(sslFactory.getHostnameVerifier())
                .build();
    }

    @Autowired
    public void unirest(SSLFactory sslFactory) {
        Unirest.primaryInstance()
                .config()
                .sslContext(sslFactory.getSslContext())
                .protocols(sslFactory.getSslParameters().getProtocols())
                .ciphers(sslFactory.getSslParameters().getCipherSuites())
                .hostnameVerifier(sslFactory.getHostnameVerifier());
    }

    @Bean
    public Retrofit retrofit(@Qualifier("okHttpClient") OkHttpClient okHttpClient) {
        return new Retrofit.Builder()
                .client(okHttpClient)
                .baseUrl(Constants.getServerUrl())
                .addConverterFactory(GsonConverterFactory.create(new GsonBuilder().setLenient().create()))
                .build();
    }

    @Bean
    public AsyncHttpClient asyncHttpClient(SSLFactory sslFactory) throws SSLException {
        var sslContext = NettySslUtils.forClient(sslFactory).build();

        var clientConfigBuilder = dispatch.Http.defaultClientBuilder()
                .setSslContext(sslContext);

        return Dsl.asyncHttpClient(clientConfigBuilder);
    }

    @Bean
    public Feign.Builder feignWithOldJdkHttpClient(SSLFactory sslFactory) {
        return Feign.builder()
                .client(new feign.Client.Default(sslFactory.getSslSocketFactory(), sslFactory.getHostnameVerifier()));
    }

    @Bean
    public Feign.Builder feignWithOkHttpClient(@Qualifier("okHttpClient") OkHttpClient okHttpClient) {
        return Feign.builder()
                .client(new feign.okhttp.OkHttpClient(okHttpClient));
    }

    @Bean
    public Feign.Builder feignWithApacheHttpClient(org.apache.http.impl.client.CloseableHttpClient httpClient) {
        return Feign.builder()
                .client(new ApacheHttpClient(httpClient));
    }

    @Bean
    public Feign.Builder feignWithApache5HttpClient(org.apache.hc.client5.http.impl.classic.CloseableHttpClient httpClient) {
        return Feign.builder()
                .client(new ApacheHttp5Client(httpClient));
    }

    @Bean
    public Feign.Builder feignWithGoogleHttpClient(HttpTransport httpTransport) {
        return Feign.builder()
                .client(new GoogleHttpClient(httpTransport));
    }

    @Bean
    public Feign.Builder feignWithJdkHttpClient(@Qualifier("jdkHttpClient") HttpClient httpClient) {
        return Feign.builder()
                .client(new Http2Client(httpClient));
    }

    @Bean
    public Methanol methanol(SSLFactory sslFactory) {
        return Methanol.newBuilder()
                .sslContext(sslFactory.getSslContext())
                .sslParameters(sslFactory.getSslParameters())
                .build();
    }

    @Bean
    public io.vertx.ext.web.client.WebClient vertxWebClient(SSLFactory sslFactory) {
        var clientOptions = new WebClientOptions();

        if (Constants.getServerUrl().contains("https")) {
            clientOptions.setSsl(true);

            sslFactory.getKeyManager()
                    .map(KeyCertOptions::wrap)
                    .ifPresent(clientOptions::setKeyCertOptions);

            sslFactory.getTrustManager()
                    .map(TrustOptions::wrap)
                    .ifPresent(clientOptions::setTrustOptions);

            sslFactory.getCiphers().forEach(clientOptions::addEnabledCipherSuite);
            sslFactory.getProtocols().forEach(clientOptions::addEnabledSecureTransportProtocol);
        }

        return io.vertx.ext.web.client.WebClient.create(Vertx.vertx(), clientOptions);
    }

}
