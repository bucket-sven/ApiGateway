package com.sunshine.api.entity

import javax.persistence.Column
import javax.persistence.Entity

@Entity
class AppUserToken : BaseEntity() {
    @Column(unique = true)
    var token: String? = null
}