package com.spring.skeleton.entity;

import com.spring.skeleton.model.Membership;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "membership")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class MembershipEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(nullable = false)
    private Long id;
    @Column(nullable = false)
    private String name;
    @Column(nullable = false)
    private Integer price;
    @Column(nullable = false)
    private Integer duration;

    public MembershipEntity(String name, Integer price, Integer duration) {
        this.id = null;
        this.name = name;
        this.price = price;
        this.duration = duration;
    }
}
