package com.sunshine.api.filter

import com.alibaba.fastjson.JSON
import com.netflix.zuul.ZuulFilter
import com.netflix.zuul.context.RequestContext
import com.sunshine.api.repository.AppUserTokenRepository
import com.sunshine.api.util.Constants
import com.sunshine.api.util.HttpUtil
import com.sunshine.api.util.RedisUtil
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpMethod
import org.springframework.http.MediaType
import org.springframework.stereotype.Component
import javax.servlet.http.HttpServletRequest

/**
 * 权限验证组件
 */
@Component
class TokenFilter : ZuulFilter() {
    @Autowired
    private lateinit var redisUtil: RedisUtil
    @Autowired
    private lateinit var appUserTokenRepository: AppUserTokenRepository

    companion object {
        const val TOKEN_PREFIX = "user:token:"
        const val CLIENT_TOKEN_NAME = "token"
        val UNAUTHORIZED = HttpUtil.genResponseBodyString(401)
    }

    override fun filterOrder(): Int {
        return 0
    }

    override fun filterType(): String {
        return "pre"
    }

    override fun shouldFilter(): Boolean {
        return true
    }

    override fun run(): Any? {
        val context = RequestContext.getCurrentContext()
        val request = context.request
        var token: String? = request.getParameter(CLIENT_TOKEN_NAME)
        request.setAttribute(Constants.REQUEST_PROCESS_START_TIME, System.currentTimeMillis())

        if (token == null && request.method != HttpMethod.GET.name) {
            val mediaType: String? = request.getHeader("Content-Type")
            if (mediaType != null && mediaType.contains("application/json")) {
                val body = readBody(request)
                val obj = JSON.parseObject(body)[CLIENT_TOKEN_NAME]
                if (obj != null) {
                    token = obj.toString()
                }
            }
        }
        token ?: return failedUnauthorized(context)
        val userToken = redisUtil.fetch(TOKEN_PREFIX + token, 3600) {
            appUserTokenRepository.findByToken(token)
        }
        userToken ?: return failedUnauthorized(context)
        return null
    }

    private fun failedUnauthorized(context: RequestContext) {
        context.setSendZuulResponse(false)
        context.response.setHeader("Content-Type", MediaType.APPLICATION_JSON_UTF8_VALUE)
        context.responseBody = UNAUTHORIZED
        context.responseStatusCode = 401
    }

    private fun readBody(request: HttpServletRequest): String {
        val inputStream = request.inputStream
        try {
            val sb = StringBuffer()
            while (true) {
                val bytes = ByteArray(1024)
                val len = inputStream.read(bytes)
                if (len <= 0) {
                    break
                }
                sb.append(String(bytes, 0, len))
            }
            return sb.toString()
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            try {
                inputStream.close()
            } catch (e2: Exception) {
                e2.printStackTrace()
            }
        }
        return "{}"
    }
}