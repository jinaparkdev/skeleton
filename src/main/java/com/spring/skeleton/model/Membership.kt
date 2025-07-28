package com.spring.skeleton.model

import com.spring.skeleton.common.Label
import com.spring.skeleton.entity.MembershipEntity
import java.time.Instant

interface HasMembership {
    val membership: Label<Long>
    val status: MembershipStatus
    val startDate: Instant
    val endDate: Instant
}

data class Membership(
    val id: Long,
    val name: String,
    val price: Int,
    val duration: Int,
    override val company: Label<Long>
) : BelongsToCompany {
    constructor(entity: MembershipEntity) : this(
        entity.id,
        entity.name,
        entity.price,
        entity.duration,
        Label(entity.company.name, entity.company.id)
    )
}

enum class MembershipStatus {
    New,
    Rejoined,
    Expired;

    companion object {

        @JvmStatic
        fun fromString(value: String): MembershipStatus = valueOf(value)
    }
}
