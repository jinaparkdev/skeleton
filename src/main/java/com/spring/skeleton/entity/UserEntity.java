package com.spring.skeleton.entity;

import jakarta.persistence.*;
import lombok.Getter;

import java.sql.Timestamp;

@Entity
@Table(name = "user")
@Getter
public class UserEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(nullable = false)
    private Long id;

    @Column(nullable = false)
    private String name;
    @Column(nullable = false)
    private String phone;
    @Column(nullable = false)
    private String status;
    @Column(nullable = false)
    private Timestamp createdAt;
    @Column(nullable = false)
    private Timestamp updatedAt;
}
