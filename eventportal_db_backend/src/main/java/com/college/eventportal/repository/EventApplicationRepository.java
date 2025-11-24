package com.college.eventportal.repository;

import com.college.eventportal.entity.EventApplication;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.List;

public interface EventApplicationRepository extends JpaRepository<EventApplication, Long> {

    boolean existsByEventIdAndStudentRegistrationNo(Long eventId, String studentRegistrationNo);

    Optional<EventApplication> findFirstByEventIdAndStudentRegistrationNo(Long eventId, String studentRegistrationNo);

    List<EventApplication> findByEventId(Long eventId);

    // For "my applications"
    List<EventApplication> findByStudentRegistrationNo(String studentRegistrationNo);
}
