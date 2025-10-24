package com.lms.Repository;

import com.lms.Entity.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface StudentRepository extends JpaRepository<Student, Long> {
    
    // Basic CRUD operations are provided by JpaRepository
    
    // Custom query methods
    List<Student> findByNameContainingIgnoreCase(String name);
    
    List<Student> findByEmailContainingIgnoreCase(String email);
    
    Optional<Student> findByStudentId(String studentId);
    
    Optional<Student> findByEmail(String email);
    
    boolean existsByEmail(String email);
    
    boolean existsByStudentId(String studentId);
    
    // Custom search query
    @Query("SELECT s FROM Student s WHERE LOWER(s.name) LIKE LOWER(CONCAT('%', :keyword, '%')) OR LOWER(s.email) LIKE LOWER(CONCAT('%', :keyword, '%')) OR LOWER(s.studentId) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    List<Student> searchStudents(@Param("keyword") String keyword);
}