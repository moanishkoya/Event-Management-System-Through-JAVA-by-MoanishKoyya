package com.college.eventportal.repository;

import com.college.eventportal.entity.Event;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface EventRepository extends JpaRepository<Event, Long> {

    List<Event> findByStatusAndEventDateGreaterThanEqual(String status, LocalDate date);

    List<Event> findByStatusAndEventDateLessThanEqual(String status, LocalDate date);

    // For approvals list
    List<Event> findByStatus(String status);

    // For "my created events"
    List<Event> findByCreatedById(Long userId);
}
