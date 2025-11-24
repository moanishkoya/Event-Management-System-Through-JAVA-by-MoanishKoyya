package com.college.eventportal.repository;

import com.college.eventportal.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    boolean existsByEmailIgnoreCase(String email);
    boolean existsByRegistrationNo(String registrationNo);

    Optional<User> findByEmailIgnoreCase(String email);
    Optional<User> findByRegistrationNo(String registrationNo);
}
