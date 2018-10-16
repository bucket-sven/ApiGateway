package com.sunshine.api.util

import com.alibaba.fastjson.JSON
import org.springframework.http.HttpStatus
import java.text.SimpleDateFormat
import java.util.*

object HttpUtil {
    private fun genResponseBody(status: Int): Map<String, Any?> {
        val reason = HttpStatus.resolve(status)?.reasonPhrase
        return mapOf(
                "timestamp" to SimpleDateFormat(Constants.DATE_FORMAT).format(Date()),
                "status" to status,
                "error" to reason,
                "message" to reason
        )
    }

    fun genResponseBodyString(status: Int): String {
        return JSON.toJSONString(genResponseBody(status))
    }

    fun genExceptionResponseBody(status: Int, exception: String): Map<String, Any?> {
        return mapOf(
                "timestamp" to SimpleDateFormat(Constants.DATE_FORMAT).format(Date()),
                "status" to status,
                "error" to HttpStatus.resolve(status)?.reasonPhrase,
                "message" to exception
        )
    }
}