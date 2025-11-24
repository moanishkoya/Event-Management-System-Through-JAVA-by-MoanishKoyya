package com.college.eventportal.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "event_applications")
public class EventApplication {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Many applications → 1 event
    @ManyToOne(optional = false)
    @JoinColumn(name = "event_id")
    private Event event;

    // student registration no (or email)
    private String studentRegistrationNo;

    private LocalDateTime appliedAt;

    // APPLIED / CANCELLED
    private String status;

    public EventApplication() {}

    @PrePersist
    public void onCreate() {
        this.appliedAt = LocalDateTime.now();
        if (this.status == null) {
            this.status = "APPLIED";
        }
    }

    // Getters & setters

    public Long getId() {
        return id;
    }

    public Event getEvent() {
        return event;
    }

    public void setEvent(Event event) {
        this.event = event;
    }

    public String getStudentRegistrationNo() {
        return studentRegistrationNo;
    }

    public void setStudentRegistrationNo(String studentRegistrationNo) {
        this.studentRegistrationNo = studentRegistrationNo;
    }

    public LocalDateTime getAppliedAt() {
        return appliedAt;
    }

    public void setAppliedAt(LocalDateTime appliedAt) {
        this.appliedAt = appliedAt;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
