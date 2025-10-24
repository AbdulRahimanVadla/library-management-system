package com.lms.Repository;

import com.lms.Entity.BorrowRecord;
import com.lms.Entity.Book;
import com.lms.Entity.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface BorrowRecordRepository extends JpaRepository<BorrowRecord, Long> {
    
    // Find all active borrow records for a student
    List<BorrowRecord> findByStudentAndIsReturnedFalse(Student student);
    
    // Find active borrow record for a book
    List<BorrowRecord> findByBookAndIsReturnedFalse(Book book);
    
    // Find overdue books
    List<BorrowRecord> findByIsReturnedFalseAndDueDateBefore(LocalDate date);
    
    // Find specific active borrow record - FIXED: This method is critical for borrow validation
    Optional<BorrowRecord> findByBookAndStudentAndIsReturnedFalse(Book book, Student student);
    
    // Find all borrow records for a student
    List<BorrowRecord> findByStudentOrderByBorrowDateDesc(Student student);
    
    // Find all borrow records for a book
    List<BorrowRecord> findByBookOrderByBorrowDateDesc(Book book);
    
    // Count active borrows by student
    long countByStudentAndIsReturnedFalse(Student student);
    
    // Find all active borrow records
    List<BorrowRecord> findByIsReturnedFalse();
    
    // Find all borrow records (including returned)
    List<BorrowRecord> findAllByOrderByBorrowDateDesc();
    
    // Custom query for borrowing history with pagination
    @Query("SELECT br FROM BorrowRecord br WHERE br.student.id = :studentId ORDER BY br.borrowDate DESC")
    List<BorrowRecord> findBorrowHistoryByStudentId(@Param("studentId") Long studentId);
    
    // Find borrow records by date range
    @Query("SELECT br FROM BorrowRecord br WHERE br.borrowDate BETWEEN :startDate AND :endDate ORDER BY br.borrowDate DESC")
    List<BorrowRecord> findByBorrowDateBetween(@Param("startDate") LocalDate startDate, 
                                              @Param("endDate") LocalDate endDate);
    
    // Count total borrows
    long count();
    
    // Count active borrows
    long countByIsReturnedFalse();
    
    // Count returned borrows
    long countByIsReturnedTrue();
}