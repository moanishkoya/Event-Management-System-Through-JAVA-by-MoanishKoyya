package com.college.eventportal.entity;

import jakarta.persistence.*;

@Entity
@Table(
    name = "users",
    uniqueConstraints = {
        @UniqueConstraint(columnNames = "email"),
        @UniqueConstraint(columnNames = "registrationNo")
    }
)
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Full name
    @Column(nullable = false)
    private String name;

    // Email login (optional but useful)
    @Column(nullable = false)
    private String email;

    // College registration number
    @Column(nullable = false)
    private String registrationNo;

    // Hashed password (BCrypt)
    @Column(nullable = false)
    private String passwordHash;

    // STUDENT / DEAN / MENTOR / CLUB_HEAD
    @Column(nullable = false)
    private String role = "STUDENT";

    private boolean active = true;

    public User() {}

    // Getters & setters

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getRegistrationNo() {
        return registrationNo;
    }

    public void setRegistrationNo(String registrationNo) {
        this.registrationNo = registrationNo;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }
}
