package com.college.eventportal.controller;

import com.college.eventportal.entity.ClubRequest;
import com.college.eventportal.repository.ClubRequestRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/clubs")
@CrossOrigin
public class ClubController {

    private final ClubRequestRepository clubRequestRepository;

    public ClubController(ClubRequestRepository clubRequestRepository) {
        this.clubRequestRepository = clubRequestRepository;
    }

    // DTO for response
    public static class ClubRequestDto {
        public Long id;
        public String clubName;
        public String description;
        public String category;
        public String proposedMentorName;
        public List<String> memberRegistrationNos;
        public String status;

        public ClubRequestDto(ClubRequest cr) {
            this.id = cr.getId();
            this.clubName = cr.getClubName();
            this.description = cr.getDescription();
            this.category = cr.getCategory();
            this.proposedMentorName = cr.getProposedMentorName();
            this.status = cr.getStatus();
            if (cr.getMemberRegistrationNos() != null) {
                this.memberRegistrationNos = Arrays.stream(cr.getMemberRegistrationNos().split(","))
                        .map(String::trim)
                        .filter(s -> !s.isEmpty())
                        .collect(Collectors.toList());
            } else {
                this.memberRegistrationNos = List.of();
            }
        }
    }

    // 🔹 Create a new club request
    @PostMapping("/requests")
    public ResponseEntity<ClubRequestDto> createClubRequest(@RequestBody Map<String, Object> body) {
        ClubRequest cr = new ClubRequest();

        cr.setClubName((String) body.getOrDefault("clubName", ""));
        cr.setDescription((String) body.getOrDefault("description", ""));
        cr.setCategory((String) body.getOrDefault("category", ""));
        cr.setProposedMentorName((String) body.getOrDefault("proposedMentorName", ""));

        Object membersObj = body.get("memberRegistrationNos");
        if (membersObj instanceof List<?> list) {
            String joined = list.stream()
                    .map(String::valueOf)
                    .map(String::trim)
                    .collect(Collectors.joining(","));
            cr.setMemberRegistrationNos(joined);
        }

        ClubRequest saved = clubRequestRepository.save(cr);
        return ResponseEntity.ok(new ClubRequestDto(saved));
    }

    // 🔹 Get all club requests
    @GetMapping("/requests")
    public List<ClubRequestDto> getAllRequests() {
        return clubRequestRepository.findAll()
                .stream()
                .map(ClubRequestDto::new)
                .collect(Collectors.toList());
    }
}
