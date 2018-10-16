package com.sunshine.api.entity

import org.hibernate.annotations.UpdateTimestamp
import org.springframework.data.annotation.CreatedDate
import java.util.*
import javax.persistence.*

@MappedSuperclass
abstract class BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null

    @CreatedDate
    @Column
    var createTime: Date? = null

    @UpdateTimestamp
    @Column
    var updateTime: Date? = null
}