package com.spring.skeleton.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "membership")
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

    @OneToOne
    @JoinColumn(name = "company_id", nullable = false)
    private CompanyEntity company;

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Integer getPrice() {
        return price;
    }

    public Integer getDuration() {
        return duration;
    }

    public CompanyEntity getCompany() {
        return company;
    }

    public MembershipEntity(String name, Integer price, Integer duration, CompanyEntity company) {
        this.id = null;
        this.name = name;
        this.price = price;
        this.duration = duration;
        this.company = company;
    }
}
