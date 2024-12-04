package com.spring.skeleton.model

import java.sql.Timestamp

data class User(
    val id: Long,
    val name: String,
    val phone: String,
    val createdAt: Timestamp,
    val updatedAt: Timestamp,
    override val membership: Membership,
    override val status: MembershipStatus
) : HasMembership
