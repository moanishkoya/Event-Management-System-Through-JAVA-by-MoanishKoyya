package com.college.eventportal.controller;

import com.college.eventportal.entity.User;
import com.college.eventportal.repository.UserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin
public class AuthController {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthController(UserRepository userRepository,
                          PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    // DTOs (just maps, to keep it simple)

    // 🔹 REGISTER
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody Map<String, Object> body) {
        String name = asString(body.get("name"));
        String email = asString(body.get("email"));
        String regNo = asString(body.get("registrationNo"));
        String password = asString(body.get("password"));

        if (name.isBlank() || email.isBlank() || regNo.isBlank() || password.isBlank()) {
            return ResponseEntity.badRequest().body(Map.of("message", "Missing required fields"));
        }

        if (userRepository.existsByEmailIgnoreCase(email)) {
            return ResponseEntity.badRequest().body(Map.of("message", "Email already in use"));
        }

        if (userRepository.existsByRegistrationNo(regNo)) {
            return ResponseEntity.badRequest().body(Map.of("message", "Registration number already in use"));
        }

        User user = new User();
        user.setName(name);
        user.setEmail(email);
        user.setRegistrationNo(regNo);
        user.setPasswordHash(passwordEncoder.encode(password));
        user.setRole("STUDENT"); // default role

        userRepository.save(user);

        return ResponseEntity.ok(Map.of("message", "Registered successfully"));
    }

    // 🔹 LOGIN
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, Object> body) {
        String identifier = asString(body.get("email"));           // try email
        String regNo = asString(body.get("registrationNo"));       // or registrationNo
        String password = asString(body.get("password"));

        if (password.isBlank()) {
            return ResponseEntity.badRequest().body(Map.of("message", "Password is required"));
        }

        Optional<User> userOpt = Optional.empty();

        if (!identifier.isBlank()) {
            userOpt = userRepository.findByEmailIgnoreCase(identifier);
        }
        if (userOpt.isEmpty() && !regNo.isBlank()) {
            userOpt = userRepository.findByRegistrationNo(regNo);
        }

        if (userOpt.isEmpty()) {
            return ResponseEntity.status(401).body(Map.of("message", "Invalid credentials"));
        }

        User user = userOpt.get();

        if (!user.isActive()) {
            return ResponseEntity.status(403).body(Map.of("message", "User is disabled"));
        }

        if (!passwordEncoder.matches(password, user.getPasswordHash())) {
            return ResponseEntity.status(401).body(Map.of("message", "Invalid credentials"));
        }

        // For now, still dummy token, but user-specific
        String token = "dummy-jwt-" + user.getId();

        Map<String, Object> resp = new HashMap<>();
        resp.put("token", token);
        resp.put("name", user.getName());
        resp.put("roles", List.of("ROLE_" + user.getRole())); // e.g. ROLE_STUDENT

        return ResponseEntity.ok(resp);
    }

    private String asString(Object o) {
        return o == null ? "" : o.toString().trim();
    }
}
