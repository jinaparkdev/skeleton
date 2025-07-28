package com.spring.skeleton.model

import com.spring.skeleton.common.Label
import com.spring.skeleton.entity.MembershipMappingEntity
import java.time.Instant

data class Member(
    var id: Long,
    var name: String,
    override var membership: Label<Long>,
    override var startDate: Instant,
    override var endDate: Instant,
    override var status: MembershipStatus
) : HasMembership {

    constructor(entity: MembershipMappingEntity) : this(
        id = entity.member.id,
        name = entity.member.name,
        membership = Label(
            entity.membership.name,
            entity.membership.id
        ),
        startDate = entity.startDate,
        endDate = entity.endDate,
        status = MembershipStatus.valueOf(entity.status)
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
