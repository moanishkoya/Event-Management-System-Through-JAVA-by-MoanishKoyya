package com.college.eventportal.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "club_requests")
public class ClubRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String clubName;
    private String description;
    private String category;
    private String proposedMentorName;

    // Comma-separated registration numbers
    @Column(length = 2000)
    private String memberRegistrationNos;

    // PENDING / APPROVED / REJECTED
    private String status;

    private LocalDateTime createdAt;

    public ClubRequest() {}

    @PrePersist
    public void onCreate() {
        this.createdAt = LocalDateTime.now();
        if (this.status == null) {
            this.status = "PENDING";
        }
    }

    // Getters & setters

    public Long getId() {
        return id;
    }

    public String getClubName() {
        return clubName;
    }

    public void setClubName(String clubName) {
        this.clubName = clubName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getProposedMentorName() {
        return proposedMentorName;
    }

    public void setProposedMentorName(String proposedMentorName) {
        this.proposedMentorName = proposedMentorName;
    }

    public String getMemberRegistrationNos() {
        return memberRegistrationNos;
    }

    public void setMemberRegistrationNos(String memberRegistrationNos) {
        this.memberRegistrationNos = memberRegistrationNos;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}
