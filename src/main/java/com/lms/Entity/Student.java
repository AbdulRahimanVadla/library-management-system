package com.lms.Entity;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "students")
public class Student {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", nullable = false, length = 100)
    private String name;

    @Column(name = "email", nullable = false, length = 100, unique = true)
    private String email;

    @Column(name = "student_id", nullable = false, length = 50, unique = true)
    private String studentId;

    @OneToMany(mappedBy = "student", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<BorrowRecord> borrowRecords = new ArrayList<>();

    // Transient field for active borrow count (not stored in database)
    @Transient
    private int activeBorrowCount = 0;

    // Constructors
    public Student() {}

    public Student(String name, String email, String studentId) {
        this.name = name;
        this.email = email;
        this.studentId = studentId;
    }

    // Getters and Setters
    public Long getId() { 
        return id; 
    }
    
    public void setId(Long id) { 
        this.id = id; 
    }

    public String getName() { 
        return name; 
    }
    
    public void setName(String name) { 
        this.name = name; 
    }

    public String getEmail() { 
        return email; 
    }
    
    public void setEmail(String email) { 
        this.email = email; 
    }

    public String getStudentId() { 
        return studentId; 
    }
    
    public void setStudentId(String studentId) { 
        this.studentId = studentId; 
    }

    public List<BorrowRecord> getBorrowRecords() { 
        return borrowRecords; 
    }
    
    public void setBorrowRecords(List<BorrowRecord> borrowRecords) { 
        this.borrowRecords = borrowRecords; 
    }

    public int getActiveBorrowCount() { 
        return activeBorrowCount; 
    }
    
    public void setActiveBorrowCount(int activeBorrowCount) { 
        this.activeBorrowCount = activeBorrowCount; 
    }

    // Helper method to calculate active borrows
    public int calculateActiveBorrows() {
        if (borrowRecords == null) {
            return 0;
        }
        return (int) borrowRecords.stream()
                .filter(record -> !record.isReturned())
                .count();
    }

    @Override
    public String toString() {
        return "Student{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", studentId='" + studentId + '\'' +
                ", activeBorrowCount=" + activeBorrowCount +
                '}';
    }

    // equals and hashCode methods
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Student student = (Student) o;

        if (id != null ? !id.equals(student.id) : student.id != null) return false;
        if (name != null ? !name.equals(student.name) : student.name != null) return false;
        if (email != null ? !email.equals(student.email) : student.email != null) return false;
        return studentId != null ? studentId.equals(student.studentId) : student.studentId == null;
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + (email != null ? email.hashCode() : 0);
        result = 31 * result + (studentId != null ? studentId.hashCode() : 0);
        return result;
    }
}