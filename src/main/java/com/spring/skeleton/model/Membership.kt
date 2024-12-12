package com.spring.skeleton.model

import com.spring.skeleton.entity.MembershipEntity

interface HasMembership {
    val membership: Membership
    val status: MembershipStatus
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
}
