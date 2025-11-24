package com.college.eventportal.config;

import com.college.eventportal.entity.User;
import com.college.eventportal.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class DataInitializer {

    @Bean
    public CommandLineRunner initDefaultUsers(UserRepository userRepository,
                                              PasswordEncoder passwordEncoder) {
        return args -> {

            // Dean
            if (userRepository.findByEmailIgnoreCase("dean@college.com").isEmpty()) {
                User dean = new User();
                dean.setName("Dean User");
                dean.setEmail("dean@college.com");
                dean.setRegistrationNo("DEAN001");
                dean.setPasswordHash(passwordEncoder.encode("dean123"));
                dean.setRole("DEAN");
                dean.setActive(true);
                userRepository.save(dean);
            }

            // Mentor
            if (userRepository.findByEmailIgnoreCase("mentor@college.com").isEmpty()) {
                User mentor = new User();
                mentor.setName("Mentor User");
                mentor.setEmail("mentor@college.com");
                mentor.setRegistrationNo("MENTOR001");
                mentor.setPasswordHash(passwordEncoder.encode("mentor123"));
                mentor.setRole("MENTOR");
                mentor.setActive(true);
                userRepository.save(mentor);
            }

            // Club Head
            if (userRepository.findByEmailIgnoreCase("clubhead@college.com").isEmpty()) {
                User ch = new User();
                ch.setName("Club Head User");
                ch.setEmail("clubhead@college.com");
                ch.setRegistrationNo("CLUBHEAD001");
                ch.setPasswordHash(passwordEncoder.encode("club123"));
                ch.setRole("CLUB_HEAD");
                ch.setActive(true);
                userRepository.save(ch);
            }

            // Sample student
            if (userRepository.findByEmailIgnoreCase("student@college.com").isEmpty()) {
                User st = new User();
                st.setName("Student User");
                st.setEmail("student@college.com");
                st.setRegistrationNo("STUD001");
                st.setPasswordHash(passwordEncoder.encode("student123"));
                st.setRole("STUDENT");
                st.setActive(true);
                userRepository.save(st);
            }
        };
    }
}
