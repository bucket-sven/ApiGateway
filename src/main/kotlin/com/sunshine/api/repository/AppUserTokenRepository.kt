package com.sunshine.api.repository

import com.sunshine.api.entity.AppUserToken
import org.springframework.data.repository.PagingAndSortingRepository

interface AppUserTokenRepository : PagingAndSortingRepository<AppUserToken, Long> {
    fun findByToken(token: String): AppUserToken?
}