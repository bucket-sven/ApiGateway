package com.sunshine.api.controller

import com.sunshine.api.repository.AppUserTokenRepository
import com.sunshine.api.util.HttpUtil
import com.sunshine.api.util.RedisUtil
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.web.servlet.error.AbstractErrorController
import org.springframework.boot.web.servlet.error.ErrorAttributes
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import javax.servlet.http.HttpServletRequest

@RestController
class ErrorHandlerController(errorAttributes: ErrorAttributes) : AbstractErrorController(errorAttributes, null) {
    @Autowired
    private lateinit var userTokenRepository: AppUserTokenRepository
    @Autowired
    private lateinit var redisUtil: RedisUtil

    override fun getErrorPath(): String {
        return "/error"
    }

    @RequestMapping("/error")
    fun error(request: HttpServletRequest): Any {
        val info = getErrorAttributes(request, true)
        return HttpUtil.genExceptionResponseBody(info["status"].toString().toInt(), info["message"].toString())
    }

    @RequestMapping("/")
    fun index(): Any? {
        val data = redisUtil.fetchOptional("temp_users_1") {
            userTokenRepository.findById(1)
        }
        return data
    }
}
