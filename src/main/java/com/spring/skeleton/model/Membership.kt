package com.spring.skeleton.model

import com.spring.skeleton.entity.MembershipEntity
import java.time.Instant

interface HasMembership {
    val membership: Membership
    val status: MembershipStatus
    val startDate: Instant
    val endDate: Instant
}

data class Membership(
    val id: Long,
    val name: String,
    val price: Int,
    val duration: Int
) {
    constructor(entity: MembershipEntity) : this(entity.id, entity.name, entity.price, entity.duration)
}

enum class MembershipStatus {
    New,
    ReRegistered,
    Expired;

    companion object {

        @JvmStatic
        fun fromString(value: String): MembershipStatus {
            return when (value) {
                "New" -> New
                "ReRegistered" -> ReRegistered
                "Expired" -> Expired
                else -> throw IllegalArgumentException("Invalid value: $value")
            }
        }
    }
}
