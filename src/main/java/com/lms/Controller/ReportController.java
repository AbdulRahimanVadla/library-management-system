package com.lms.Controller;

import com.lms.Service.BorrowRecordService;
import com.lms.Service.BookService;
import com.lms.Service.StudentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/reports")
public class ReportController {
    
    @Autowired
    private BorrowRecordService borrowRecordService;
    
    @Autowired
    private BookService bookService;
    
    @Autowired
    private StudentService studentService;
    
    @GetMapping("/dashboard")
    public String showReports(Model model) {
        try {
            // Basic statistics
            model.addAttribute("totalBooks", bookService.count());
            model.addAttribute("availableBooks", bookService.findAvailableBooks().size());
            model.addAttribute("totalStudents", studentService.count());
            model.addAttribute("activeBorrows", borrowRecordService.getActiveBorrowsCount());
            model.addAttribute("overdueBooks", borrowRecordService.getOverdueBooks().size());
            model.addAttribute("totalBorrows", borrowRecordService.getTotalBorrows());
            model.addAttribute("returnedBorrows", borrowRecordService.getReturnedBorrowsCount());
            
            return "reports_dashboard";
        } catch (Exception e) {
            // If there's an error, set default values
            model.addAttribute("totalBooks", 0);
            model.addAttribute("availableBooks", 0);
            model.addAttribute("totalStudents", 0);
            model.addAttribute("activeBorrows", 0);
            model.addAttribute("overdueBooks", 0);
            model.addAttribute("totalBorrows", 0);
            model.addAttribute("returnedBorrows", 0);
            model.addAttribute("errorMessage", "Error loading reports: " + e.getMessage());
            return "reports_dashboard";
        }
    }
    
    @GetMapping("/overdue")
    public String showOverdueReport(Model model) {
        try {
            model.addAttribute("overdueBooks", borrowRecordService.getOverdueBooks());
            model.addAttribute("overdueCount", borrowRecordService.getOverdueBooks().size());
            return "reports_overdue";
        } catch (Exception e) {
            model.addAttribute("errorMessage", "Error loading overdue report: " + e.getMessage());
            return "reports_overdue";
        }
    }
    
    @GetMapping("/borrowing")
    public String showBorrowingReport(Model model) {
        try {
            model.addAttribute("activeBorrows", borrowRecordService.getAllActiveBorrows());
            model.addAttribute("activeBorrowsCount", borrowRecordService.getActiveBorrowsCount());
            
            // Recent borrows (last 30 days)
            LocalDate thirtyDaysAgo = LocalDate.now().minusDays(30);
            model.addAttribute("recentBorrows", borrowRecordService.getBorrowsByDateRange(thirtyDaysAgo, LocalDate.now()));
            
            return "reports_borrowing";
        } catch (Exception e) {
            model.addAttribute("errorMessage", "Error loading borrowing report: " + e.getMessage());
            return "reports_borrowing";
        }
    }
    
    @GetMapping("/students")
    public String showStudentReports(Model model) {
        try {
            // Get all students and add active borrow count to each student
            List<com.lms.Entity.Student> students = studentService.findAll().stream()
                .map(student -> {
                    int activeBorrowCount = borrowRecordService.getActiveBorrowsByStudent(student.getId()).size();
                    // Create a wrapper or set the count (we'll use a simple approach)
                    student.setActiveBorrowCount(activeBorrowCount);
                    return student;
                })
                .collect(Collectors.toList());
            
            model.addAttribute("students", students);
            model.addAttribute("totalStudents", studentService.count());
            
            // Students with active borrows
            long studentsWithActiveBorrows = students.stream()
                .filter(student -> student.getActiveBorrowCount() > 0)
                .count();
            model.addAttribute("studentsWithActiveBorrows", studentsWithActiveBorrows);
            
            return "reports_students";
        } catch (Exception e) {
            model.addAttribute("errorMessage", "Error loading student report: " + e.getMessage());
            return "reports_students";
        }
    }
    
    @GetMapping("/books")
    public String showBookReports(Model model) {
        try {
            model.addAttribute("books", bookService.findAll());
            model.addAttribute("totalBooks", bookService.count());
            model.addAttribute("availableBooks", bookService.findAvailableBooks().size());
            model.addAttribute("borrowedBooks", borrowRecordService.getActiveBorrowsCount());
            
            return "reports_books";
        } catch (Exception e) {
            model.addAttribute("errorMessage", "Error loading book report: " + e.getMessage());
            return "reports_books";
        }
    }
}