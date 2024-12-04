package com.spring.skeleton.model

interface HasMembership {
    val membership: Membership
    val status: MembershipStatus
}

data class Membership(
    val id: Long,
    val name: String,
    val price: Int,
    val duration: Int
)

enum class MembershipStatus {
    New,
    ReRegistered,
    Expired;
}
