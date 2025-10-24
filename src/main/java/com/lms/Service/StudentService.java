package com.lms.Service;

import com.lms.Entity.Student;
import com.lms.Repository.StudentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class StudentService {
    
    @Autowired
    private StudentRepository studentRepository;
    
    // Basic CRUD operations using repository methods that definitely exist
    public List<Student> getAllStudents() {
        return studentRepository.findAll();
    }
    
    public Optional<Student> getStudentById(Long id) {
        return studentRepository.findById(id);
    }
    
    public Student saveStudent(Student student) {
        return studentRepository.save(student);
    }
    
    public void deleteStudent(Long id) {
        studentRepository.deleteById(id);
    }
    
    public void updateStudent(Long id, Student studentDetails) {
        Optional<Student> optionalStudent = studentRepository.findById(id);
        if (optionalStudent.isPresent()) {
            Student student = optionalStudent.get();
            student.setName(studentDetails.getName());
            student.setEmail(studentDetails.getEmail());
            student.setStudentId(studentDetails.getStudentId());
            studentRepository.save(student);
        } else {
            throw new RuntimeException("Student not found with id: " + id);
        }
    }
    
    public long count() {
        return studentRepository.count();
    }
    
    // Search functionality with null safety
    public List<Student> searchStudents(String keyword) {
        List<Student> allStudents = getAllStudents();
        if (keyword == null || keyword.trim().isEmpty()) {
            return allStudents;
        }
        
        String searchTerm = keyword.toLowerCase().trim();
        return allStudents.stream()
                .filter(student -> 
                    (student.getName() != null && student.getName().toLowerCase().contains(searchTerm)) ||
                    (student.getEmail() != null && student.getEmail().toLowerCase().contains(searchTerm)) ||
                    (student.getStudentId() != null && student.getStudentId().toLowerCase().contains(searchTerm))
                )
                .toList();
    }
    
    // Duplicate check without custom repository methods
    public boolean isEmailExists(String email) {
        if (email == null) return false;
        List<Student> allStudents = getAllStudents();
        return allStudents.stream()
                .anyMatch(student -> student.getEmail() != null && student.getEmail().equalsIgnoreCase(email));
    }
    
    public boolean isStudentIdExists(String studentId) {
        if (studentId == null) return false;
        List<Student> allStudents = getAllStudents();
        return allStudents.stream()
                .anyMatch(student -> student.getStudentId() != null && student.getStudentId().equalsIgnoreCase(studentId));
    }
    
    // Additional helper methods
    public Student findById(Long id) {
        Optional<Student> student = studentRepository.findById(id);
        if (student.isPresent()) {
            return student.get();
        } else {
            throw new RuntimeException("Student not found with id: " + id);
        }
    }
    
    public Student save(Student student) {
        return studentRepository.save(student);
    }
    
    public void deleteById(Long id) {
        studentRepository.deleteById(id);
    }
    
    public List<Student> findAll() {
        return studentRepository.findAll();
    }
}