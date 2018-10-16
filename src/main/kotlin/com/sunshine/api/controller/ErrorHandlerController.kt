package com.sunshine.api.controller

import com.sunshine.api.util.HttpUtil
import org.springframework.boot.autoconfigure.web.servlet.error.AbstractErrorController
import org.springframework.boot.web.servlet.error.ErrorAttributes
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import javax.servlet.http.HttpServletRequest

@RestController
class ErrorHandlerController(errorAttributes: ErrorAttributes) : AbstractErrorController(errorAttributes, null) {

    override fun getErrorPath(): String {
        return "/error"
    }

    @RequestMapping("/error")
    fun error(request: HttpServletRequest): Any {
        val info = getErrorAttributes(request, true)
        return HttpUtil.genExceptionResponseBody(info["status"].toString().toInt(), info["message"].toString())
    }
}
