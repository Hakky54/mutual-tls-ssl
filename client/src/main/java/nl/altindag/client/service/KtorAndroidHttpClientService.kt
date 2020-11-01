package nl.altindag.client.service

import io.ktor.client.HttpClient
import io.ktor.client.engine.android.Android
import nl.altindag.client.ClientType
import nl.altindag.client.ClientType.KTOR_ANDROID_HTTP_CLIENT
import nl.altindag.sslcontext.SSLFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class KtorAndroidHttpClientService(
        @Autowired(required = false)
        sslFactory: SSLFactory?
): KtorHttpClientService(
        HttpClient(Android) {
            sslFactory?.let { factory ->
                engine {
                    sslManager = { httpsURLConnection ->
                        httpsURLConnection.hostnameVerifier = factory.hostnameVerifier
                        httpsURLConnection.sslSocketFactory = factory.sslSocketFactory
                    }
                }
            }
        }
) {

    override fun getClientType(): ClientType = KTOR_ANDROID_HTTP_CLIENT

}
