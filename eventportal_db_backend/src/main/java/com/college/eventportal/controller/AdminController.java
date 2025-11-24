package com.college.eventportal.controller;

import com.college.eventportal.repository.EventRepository;
import com.college.eventportal.repository.EventApplicationRepository;
import com.college.eventportal.repository.UserRepository;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/admin")
@CrossOrigin
public class AdminController {

    private final UserRepository userRepository;
    private final EventRepository eventRepository;
    private final EventApplicationRepository eventApplicationRepository;

    public AdminController(UserRepository userRepository,
                           EventRepository eventRepository,
                           EventApplicationRepository eventApplicationRepository) {
        this.userRepository = userRepository;
        this.eventRepository = eventRepository;
        this.eventApplicationRepository = eventApplicationRepository;
    }

    @GetMapping("/summary")
    public Map<String, Object> getSummary() {

        long totalUsers = userRepository.count();
        long totalEvents = eventRepository.count();
        long totalApplications = eventApplicationRepository.count();

        long approvedEvents = eventRepository.findByStatus("APPROVED").size();
        long pendingEvents = eventRepository.findByStatus("PENDING").size();
        long completedEvents = eventRepository.findByStatus("COMPLETED").size();

        return Map.of(
                "totalUsers", totalUsers,
                "totalEvents", totalEvents,
                "approvedEvents", approvedEvents,
                "pendingEvents", pendingEvents,
                "completedEvents", completedEvents,
                "totalApplications", totalApplications
        );
    }
}
