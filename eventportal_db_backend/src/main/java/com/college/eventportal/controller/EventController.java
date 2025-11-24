package com.college.eventportal.controller;

import com.college.eventportal.entity.Event;
import com.college.eventportal.entity.EventApplication;
import com.college.eventportal.repository.EventRepository;
import com.college.eventportal.repository.EventApplicationRepository;
import com.college.eventportal.repository.UserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;

@RestController
@RequestMapping("/api/events")
@CrossOrigin
public class EventController {

    private final EventRepository eventRepository;
    private final EventApplicationRepository eventApplicationRepository;
    private final UserRepository userRepository;

    public EventController(EventRepository eventRepository,
                           EventApplicationRepository eventApplicationRepository,
                           UserRepository userRepository) {
        this.eventRepository = eventRepository;
        this.eventApplicationRepository = eventApplicationRepository;
        this.userRepository = userRepository;
    }

    public static class SimpleEvent {
        public Long id;
        public String title;
        public String description;
        public String eventDate;
        public String startTime;
        public String endTime;
        public String venueName;
        public String status;
        public String eventType;
        public String createdByName;
        public String createdByRole;

        public SimpleEvent(Event e) {
            this.id = e.getId();
            this.title = e.getTitle();
            this.description = e.getDescription();
            this.eventDate = e.getEventDate() != null ? e.getEventDate().toString() : null;
            this.startTime = e.getStartTime() != null ? e.getStartTime().toString() : null;
            this.endTime = e.getEndTime() != null ? e.getEndTime().toString() : null;
            this.venueName = e.getVenueName();
            this.status = e.getStatus();
            this.eventType = e.getEventType();
            if (e.getCreatedBy() != null) {
                this.createdByName = e.getCreatedBy().getName();
                this.createdByRole = e.getCreatedBy().getRole();
            }
        }
    }

    // ---------- PUBLIC LISTS ----------

    @GetMapping("/public/upcoming")
    public List<SimpleEvent> getUpcomingEvents() {
        LocalDate today = LocalDate.now();
        var events = eventRepository.findByStatusAndEventDateGreaterThanEqual("APPROVED", today);
        List<SimpleEvent> result = new ArrayList<>();
        for (Event e : events) {
            result.add(new SimpleEvent(e));
        }
        return result;
    }

    @GetMapping("/public/gallery")
    public List<SimpleEvent> getGalleryEvents() {
        LocalDate today = LocalDate.now();
        var events = eventRepository.findByStatusAndEventDateLessThanEqual("COMPLETED", today);
        List<SimpleEvent> result = new ArrayList<>();
        for (Event e : events) {
            result.add(new SimpleEvent(e));
        }
        return result;
    }

    // ---------- CREATE EVENT (Club Head / Mentor) ----------

    // Called like: POST /api/events/create with JSON body
    @PostMapping("/create")
    public ResponseEntity<?> createEvent(@RequestBody Map<String, Object> body) {
        String title = str(body.get("title"));
        String description = str(body.get("description"));
        String dateStr = str(body.get("eventDate"));
        String startStr = str(body.get("startTime"));
        String endStr = str(body.get("endTime"));
        String venue = str(body.get("venueName"));
        String eventType = str(body.get("eventType"));
        Long createdByUserId = body.get("createdByUserId") == null
                ? null
                : Long.valueOf(body.get("createdByUserId").toString());

        if (title.isBlank() || dateStr.isBlank()) {
            return ResponseEntity.badRequest().body(Map.of("message", "Title and date are required"));
        }

        LocalDate date = LocalDate.parse(dateStr);
        LocalTime start = startStr.isBlank() ? null : LocalTime.parse(startStr);
        LocalTime end = endStr.isBlank() ? null : LocalTime.parse(endStr);

        Event event = new Event();
        event.setTitle(title);
        event.setDescription(description);
        event.setEventDate(date);
        event.setStartTime(start);
        event.setEndTime(end);
        event.setVenueName(venue);
        event.setEventType(eventType);
        event.setStatus("PENDING"); // needs Dean approval

        if (createdByUserId != null) {
            userRepository.findById(createdByUserId).ifPresent(event::setCreatedBy);
        }

        eventRepository.save(event);

        return ResponseEntity.ok(new SimpleEvent(event));
    }

    // ---------- APPROVALS (Dean) ----------

    // List of PENDING events for Dean dashboard
    @GetMapping("/pending")
    public List<SimpleEvent> getPendingEvents() {
        var events = eventRepository.findByStatus("PENDING");
        List<SimpleEvent> result = new ArrayList<>();
        for (Event e : events) {
            result.add(new SimpleEvent(e));
        }
        return result;
    }

    @PostMapping("/{eventId}/approve")
    public ResponseEntity<?> approveEvent(@PathVariable Long eventId) {
        Optional<Event> opt = eventRepository.findById(eventId);
        if (opt.isEmpty()) {
            return ResponseEntity.status(404).body(Map.of("message", "Event not found"));
        }
        Event e = opt.get();
        e.setStatus("APPROVED");
        eventRepository.save(e);
        return ResponseEntity.ok(Map.of("message", "Event approved"));
    }

    @PostMapping("/{eventId}/reject")
    public ResponseEntity<?> rejectEvent(@PathVariable Long eventId,
                                         @RequestBody(required = false) Map<String, Object> body) {
        Optional<Event> opt = eventRepository.findById(eventId);
        if (opt.isEmpty()) {
            return ResponseEntity.status(404).body(Map.of("message", "Event not found"));
        }
        Event e = opt.get();
        e.setStatus("REJECTED");
        eventRepository.save(e);
        return ResponseEntity.ok(Map.of("message", "Event rejected"));
    }

    // ---------- USER-SPECIFIC LISTS ----------

    // /api/events/my-created?userId=123
    @GetMapping("/my-created")
    public List<SimpleEvent> getMyCreatedEvents(@RequestParam Long userId) {
        var events = eventRepository.findByCreatedById(userId);
        List<SimpleEvent> result = new ArrayList<>();
        for (Event e : events) {
            result.add(new SimpleEvent(e));
        }
        return result;
    }

    // /api/events/my-applications?registrationNo=22CSE001
    @GetMapping("/my-applications")
    public List<Map<String, Object>> getMyApplications(@RequestParam String registrationNo) {
        var apps = eventApplicationRepository.findByStudentRegistrationNo(registrationNo);
        List<Map<String, Object>> result = new ArrayList<>();
        for (EventApplication app : apps) {
            Event e = app.getEvent();
            Map<String, Object> row = new HashMap<>();
            row.put("applicationId", app.getId());
            row.put("applicationStatus", app.getStatus());
            if (e != null) {
                row.put("eventId", e.getId());
                row.put("title", e.getTitle());
                row.put("eventDate", e.getEventDate() != null ? e.getEventDate().toString() : null);
                row.put("venueName", e.getVenueName());
            }
            result.add(row);
        }
        return result;
    }

    // ---------- APPLICATIONS (already added) ----------

    @PostMapping("/{eventId}/apply")
    public ResponseEntity<?> applyForEvent(@PathVariable Long eventId,
                                           @RequestBody(required = false) Map<String, Object> body) {

        String regNo = extractRegNo(body);
        if (regNo == null || regNo.isBlank()) {
            regNo = "UNKNOWN";
        }

        Optional<Event> optEvent = eventRepository.findById(eventId);
        if (optEvent.isEmpty()) {
            return ResponseEntity.status(404).body(Map.of("message", "Event not found"));
        }
        Event event = optEvent.get();

        if (eventApplicationRepository.existsByEventIdAndStudentRegistrationNo(eventId, regNo)) {
            return ResponseEntity.ok(Map.of("message", "Already applied"));
        }

        EventApplication app = new EventApplication();
        app.setEvent(event);
        app.setStudentRegistrationNo(regNo);
        app.setStatus("APPLIED");

        eventApplicationRepository.save(app);

        return ResponseEntity.ok(Map.of("message", "Applied successfully"));
    }

    @PostMapping("/{eventId}/cancel-application")
    public ResponseEntity<?> cancelApplication(@PathVariable Long eventId,
                                               @RequestBody(required = false) Map<String, Object> body) {
        String regNo = extractRegNo(body);
        if (regNo == null || regNo.isBlank()) {
            regNo = "UNKNOWN";
        }

        var existingOpt = eventApplicationRepository
                .findFirstByEventIdAndStudentRegistrationNo(eventId, regNo);

        if (existingOpt.isEmpty()) {
            return ResponseEntity.ok(Map.of("message", "No existing application to cancel"));
        }

        EventApplication app = existingOpt.get();
        app.setStatus("CANCELLED");
        eventApplicationRepository.save(app);

        return ResponseEntity.ok(Map.of("message", "Application cancelled"));
    }

    // ---------- Helpers ----------

    private String str(Object o) {
        return o == null ? "" : o.toString().trim();
    }

    private String extractRegNo(Map<String, Object> body) {
        if (body == null) return null;
        Object val = body.get("registrationNo");
        if (val == null) {
            val = body.get("regNo");
        }
        return val == null ? null : val.toString().trim();
    }
}
