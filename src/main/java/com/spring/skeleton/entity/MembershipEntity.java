package com.spring.skeleton.entity;

import com.spring.skeleton.model.Membership;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Entity
@Table(name = "membership")
@Getter
@AllArgsConstructor
public class MembershipEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(nullable = false)
    private final Long id;
    @Column(nullable = false)
    private final String name;
    @Column(nullable = false)
    private final Integer price;
    @Column(nullable = false)
    private final Integer duration;

    public MembershipEntity() {
        this.id = null;
        this.name = null;
        this.price = null;
        this.duration = null;
    }

    public static Membership toModel(MembershipEntity entity) {
        return new Membership(
                entity.getId(),
                entity.getName(),
                entity.getPrice(),
                entity.getDuration()
        );
    }
}
