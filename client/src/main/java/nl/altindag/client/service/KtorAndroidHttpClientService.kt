package nl.altindag.client.service

import io.ktor.client.HttpClient
import io.ktor.client.engine.android.Android
import nl.altindag.client.ClientType
import nl.altindag.client.ClientType.KTOR_ANDROID_HTTP_CLIENT
import nl.altindag.ssl.SSLFactory
import org.springframework.stereotype.Service

@Service
class KtorAndroidHttpClientService(
    sslFactory: SSLFactory
) : KtorHttpClientService(
    HttpClient(Android) {
        engine {
            sslManager = { httpsURLConnection ->
                httpsURLConnection.hostnameVerifier = sslFactory.hostnameVerifier
                httpsURLConnection.sslSocketFactory = sslFactory.sslSocketFactory
            }
        }
    }
) {

    override fun getClientType(): ClientType = KTOR_ANDROID_HTTP_CLIENT

}
