package com.school.management.smbackend.Repositories;

import com.school.management.smbackend.Entities.Level;
import com.school.management.smbackend.Entities.Student;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface StudentRepository extends JpaRepository<Student, Long> {
    boolean existsByUsername(String username);
    Page<Student> findByUsernameContainingIgnoreCase(String username, Pageable pageable);
    Page<Student> findByLevel(Level level, Pageable pageable);
    List<Student> findByUsernameContainingIgnoreCase(String username);
}
