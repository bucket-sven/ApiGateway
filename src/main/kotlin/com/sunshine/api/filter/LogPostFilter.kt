package com.sunshine.api.filter

import com.netflix.zuul.ZuulFilter
import com.netflix.zuul.context.RequestContext
import com.sunshine.api.util.Constants
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component

/**
 * 日志打印组件
 */
@Component
class LogPostFilter : ZuulFilter() {
    private val logger = LoggerFactory.getLogger(LogPostFilter::class.java)

    override fun filterOrder(): Int {
        return 10
    }

    override fun filterType(): String {
        return "post"
    }

    override fun shouldFilter(): Boolean {
        return true
    }

    override fun run(): Any? {
        val context = RequestContext.getCurrentContext()
        val request = context.request
        val requestStartTime = request.getAttribute(Constants.REQUEST_PROCESS_START_TIME).toString().toLong()
        val now = System.currentTimeMillis()
        var url = context["requestURI"] as String
        val queryString: String? = request.queryString
        if (!queryString.isNullOrEmpty()) {
            url += "?$queryString"
        }
        logger.info("${request.method} $url ${context.responseStatusCode} ${now - requestStartTime} ms")
        return null
    }
}