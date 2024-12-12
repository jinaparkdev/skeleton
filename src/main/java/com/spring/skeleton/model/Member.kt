package com.spring.skeleton.model

import com.spring.skeleton.entity.MembershipMappingEntity
import java.sql.Timestamp
import java.time.Instant

data class Member(
    var id: Long,
    var name: String,
    var phone: String,
    override var membership: Membership,
    override var startDate: Instant,
    override var endDate: Instant,
    override var status: MembershipStatus
) : HasMembership {

    constructor(entity: MembershipMappingEntity) : this(
        id = entity.member.id,
        name = entity.member.name,
        phone = entity.member.phone,
        membership = Membership(entity.membership),
        startDate = entity.startDate,
        endDate = entity.endDate,
        status = MembershipStatus.fromString(entity.status)
    )
}

data class MemberDetail(
    val summary: Member,
    val phone: String,
    val createdAt: Instant,
    val updatedAt: Instant,
) {
    constructor(entity: MembershipMappingEntity) : this(
        summary = Member(entity),
        phone = entity.member.phone,
        createdAt = entity.member.createdAt,
        updatedAt = entity.member.updatedAt
    )
}
