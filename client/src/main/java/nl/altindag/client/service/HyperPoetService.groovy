package nl.altindag.client.service

import com.tambapps.http.hyperpoet.HttpPoet
import nl.altindag.client.ClientType
import nl.altindag.client.model.ClientResponse
import nl.altindag.ssl.SSLFactory
import okhttp3.OkHttpClient
import org.springframework.context.annotation.Bean
import org.springframework.stereotype.Component
import org.springframework.stereotype.Service

import static nl.altindag.client.ClientType.HYPER_POET_CLIENT
import static nl.altindag.client.Constants.HEADER_KEY_CLIENT_TYPE

@Service
class HyperPoetService implements RequestService {

    private final HttpPoet poet

    HyperPoetService(HttpPoet poet) {
        this.poet = poet
    }

    @Override
    ClientResponse executeRequest(String url) throws Exception {
        def headers = [:]
        headers[HEADER_KEY_CLIENT_TYPE] = getClientType().value

        poet.get(url, headers: headers) {
            new ClientResponse(it.body().string(), it.code())
        } as ClientResponse
    }

    @Override
    ClientType getClientType() {
        return HYPER_POET_CLIENT
    }

}

@Component
class HyperPoetClientConfiguration {

    @Bean
    HttpPoet httpPoet(SSLFactory sslFactory) {
        def okHttpClient = new OkHttpClient.Builder()
                .sslSocketFactory(sslFactory.getSslSocketFactory(), sslFactory.getTrustManager().orElseThrow())
                .build()

        return new HttpPoet(okHttpClient)
    }

}
