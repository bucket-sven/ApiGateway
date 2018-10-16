package com.sunshine.api.fallback

import com.sunshine.api.util.HttpUtil
import org.springframework.cloud.netflix.zuul.filters.route.FallbackProvider
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.client.ClientHttpResponse
import org.springframework.stereotype.Component
import java.io.ByteArrayInputStream
import java.io.InputStream
import java.nio.charset.Charset

/**
 * 服务熔断
 */
@Component
class ZuulFallback : FallbackProvider {
    override fun fallbackResponse(route: String?, cause: Throwable?): ClientHttpResponse {
        val unavailable = HttpStatus.SERVICE_UNAVAILABLE
        return object : ClientHttpResponse {
            override fun getStatusCode(): HttpStatus {
                return unavailable
            }

            override fun getRawStatusCode(): Int {
                return unavailable.value()
            }

            override fun getStatusText(): String {
                return unavailable.reasonPhrase
            }

            override fun close() {}

            override fun getBody(): InputStream {
                val str = HttpUtil.genResponseBodyString(unavailable.value())
                return ByteArrayInputStream(str.toByteArray(Charset.forName("UTF-8")))
            }

            override fun getHeaders(): HttpHeaders {
                val headers = HttpHeaders()
                headers.contentType = MediaType.APPLICATION_JSON_UTF8
                return headers
            }
        }
    }

    /**
     * 熔断，只能对service-id配置生效，对url无效
     */
    override fun getRoute(): String {
        return "*"
    }
}