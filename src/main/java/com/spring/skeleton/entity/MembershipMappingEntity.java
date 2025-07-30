package com.spring.skeleton.entity;

import jakarta.persistence.*;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;

@Entity
@Table(name = "membership_mapping",
       uniqueConstraints =
       @UniqueConstraint(
               name = "uk_verification_code_membership_id",
               columnNames = {"verification_code", "membership_id"}
       )
)
@Setter
@NoArgsConstructor
public class MembershipMappingEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(nullable = false)
    private Long id;

    @OneToOne
    @JoinColumn(name = "member_id", nullable = false)
    private MemberEntity member;

    @ManyToOne
    @JoinColumn(name = "membership_id", nullable = false)
    private MembershipEntity membership;

    private Instant startDate;
    private Instant endDate;
    private String status;

    @Column(nullable = false)
    private Instant createdAt;
    @Column(nullable = false)
    private Instant updatedAt;

    @Column(nullable = false)
    private String verificationCode;

    public Long getId() {
        return id;
    }

    public MemberEntity getMember() {
        return member;
    }

    public MembershipEntity getMembership() {
        return membership;
    }

    public Instant getStartDate() {
        return startDate;
    }

    public Instant getEndDate() {
        return endDate;
    }

    public String getStatus() {return status;}

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public String getVerificationCode() {return verificationCode;}

    public MembershipMappingEntity(MemberEntity member,
                                   MembershipEntity membership,
                                   Instant startDate,
                                   String status,
                                   String verificationCode) {
        Instant now = Instant.now();

        this.id = null;
        this.member = member;
        this.membership = membership;
        this.status = status;
        this.createdAt = now;
        this.updatedAt = now;
        this.startDate = startDate;
        //TODO duration에 따라서 endDate를 설정해야 함
        this.verificationCode = verificationCode != null ? verificationCode : member.getPhone()
                .substring(member.getPhone().length() - 4);
        this.endDate = startDate.plusSeconds(60 * 60 * 24 * 30);
    }
}
