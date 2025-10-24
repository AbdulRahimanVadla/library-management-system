package com.lms.Service;

import com.lms.Entity.BorrowRecord;
import com.lms.Entity.Book;
import com.lms.Entity.Student;
import com.lms.Repository.BorrowRecordRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class BorrowRecordService {
    
    @Autowired
    private BorrowRecordRepository borrowRecordRepository;
    
    @Autowired
    private BookService bookService;
    
    @Autowired
    private StudentService studentService;
    
    private static final int BORROW_DAYS = 14; // 2 weeks borrowing period
    private static final int MAX_BORROW_LIMIT = 5; // Maximum books a student can borrow
    
    public BorrowRecord borrowBook(Long bookId, Long studentId) {
        Book book = bookService.findById(bookId);
        Student student = studentService.findById(studentId);
        
        // Check if book is available
        if (!book.isAvailable()) {
            throw new IllegalStateException("Book '" + book.getTitle() + "' is not available for borrowing");
        }
        
        // Check if student already has this book
        Optional<BorrowRecord> existingRecord = borrowRecordRepository
            .findByBookAndStudentAndIsReturnedFalse(book, student);
        if (existingRecord.isPresent()) {
            throw new IllegalStateException("Student '" + student.getName() + "' already has this book");
        }
        
        // Check if student has too many books
        long activeBorrows = borrowRecordRepository.countByStudentAndIsReturnedFalse(student);
        if (activeBorrows >= MAX_BORROW_LIMIT) {
            throw new IllegalStateException("Student '" + student.getName() + "' has reached maximum borrowing limit (" + MAX_BORROW_LIMIT + " books)");
        }
        
        // Create borrow record
        LocalDate borrowDate = LocalDate.now();
        LocalDate dueDate = borrowDate.plusDays(BORROW_DAYS);
        
        BorrowRecord borrowRecord = new BorrowRecord(book, student, borrowDate, dueDate);
        
        // Update book availability
        book.setIsAvailable(false);
        bookService.save(book);
        
        return borrowRecordRepository.save(borrowRecord);
    }
    
    public BorrowRecord returnBook(Long borrowRecordId) {
        BorrowRecord borrowRecord = borrowRecordRepository.findById(borrowRecordId)
            .orElseThrow(() -> new RuntimeException("Borrow record not found with id: " + borrowRecordId));
        
        if (borrowRecord.isReturned()) {
            throw new IllegalStateException("Book is already returned");
        }
        
        // Update borrow record
        borrowRecord.setReturned(true);
        borrowRecord.setReturnDate(LocalDate.now());
        
        // Update book availability
        Book book = borrowRecord.getBook();
        book.setIsAvailable(true);
        bookService.save(book);
        
        return borrowRecordRepository.save(borrowRecord);
    }
    
    public List<BorrowRecord> getActiveBorrowsByStudent(Long studentId) {
        Student student = studentService.findById(studentId);
        return borrowRecordRepository.findByStudentAndIsReturnedFalse(student);
    }
    
    public List<BorrowRecord> getBorrowHistoryByStudent(Long studentId) {
        return borrowRecordRepository.findBorrowHistoryByStudentId(studentId);
    }
    
    public List<BorrowRecord> getOverdueBooks() {
        return borrowRecordRepository.findByIsReturnedFalseAndDueDateBefore(LocalDate.now());
    }
    
    public List<BorrowRecord> getAllActiveBorrows() {
        return borrowRecordRepository.findByIsReturnedFalse();
    }
    
    public List<BorrowRecord> getAllBorrowRecords() {
        return borrowRecordRepository.findAllByOrderByBorrowDateDesc();
    }
    
    public Optional<BorrowRecord> findById(Long id) {
        return borrowRecordRepository.findById(id);
    }
    
    public boolean isBookAvailable(Long bookId) {
        Book book = bookService.findById(bookId);
        return book.isAvailable();
    }
    
    public long getTotalBorrows() {
        return borrowRecordRepository.count();
    }
    
    public long getActiveBorrowsCount() {
        return borrowRecordRepository.countByIsReturnedFalse();
    }
    
    public long getReturnedBorrowsCount() {
        return borrowRecordRepository.countByIsReturnedTrue();
    }
    
    public List<BorrowRecord> getBorrowsByDateRange(LocalDate startDate, LocalDate endDate) {
        return borrowRecordRepository.findByBorrowDateBetween(startDate, endDate);
    }
    
    public boolean canStudentBorrowMore(Long studentId) {
        Student student = studentService.findById(studentId);
        long activeBorrows = borrowRecordRepository.countByStudentAndIsReturnedFalse(student);
        return activeBorrows < MAX_BORROW_LIMIT;
    }
    
    public int getRemainingBorrowLimit(Long studentId) {
        Student student = studentService.findById(studentId);
        long activeBorrows = borrowRecordRepository.countByStudentAndIsReturnedFalse(student);
        return (int) (MAX_BORROW_LIMIT - activeBorrows);
    }
}