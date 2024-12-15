package com.spring.skeleton.model

import com.spring.skeleton.common.Label
import com.spring.skeleton.entity.CompanyEntity

data class Company(
    val id: Long,
    val name: String,
    val email: String,
    val phone: String
) {
    constructor(entity: CompanyEntity) : this(
        entity.id,
        entity.name,
        entity.email,
        entity.phone
    )
}

interface BelongsToCompany {
    val company: Label<Long>
}
