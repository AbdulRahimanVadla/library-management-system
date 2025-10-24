package com.lms.Controller;

import com.lms.Entity.BorrowRecord;
import com.lms.Service.BorrowRecordService;
import com.lms.Service.BookService;
import com.lms.Service.StudentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/borrows")
public class BorrowController {
    
    @Autowired
    private BorrowRecordService borrowRecordService;
    
    @Autowired
    private BookService bookService;
    
    @Autowired
    private StudentService studentService;
    
    // Show borrow form
    @GetMapping("/new")
    public String showBorrowForm(Model model) {
        try {
            model.addAttribute("books", bookService.findAvailableBooks());
            model.addAttribute("students", studentService.findAll());
            return "borrow_form";
        } catch (Exception e) {
            model.addAttribute("errorMessage", "Error loading form: " + e.getMessage());
            return "borrow_form";
        }
    }
    
    // Process book borrowing
    @PostMapping("/new")
    public String borrowBook(@RequestParam Long bookId, 
                           @RequestParam Long studentId,
                           RedirectAttributes redirectAttributes) {
        try {
            BorrowRecord borrowRecord = borrowRecordService.borrowBook(bookId, studentId);
            redirectAttributes.addFlashAttribute("successMessage", 
                "Book borrowed successfully! Due date: " + borrowRecord.getDueDate());
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", 
                "Error borrowing book: " + e.getMessage());
        }
        return "redirect:/borrows/active";
    }
    
    // Show active borrows
    @GetMapping("/active")
    public String showActiveBorrows(Model model) {
        try {
            List<BorrowRecord> activeBorrows = borrowRecordService.getAllActiveBorrows();
            model.addAttribute("activeBorrows", activeBorrows);
            
            // Add statistics
            model.addAttribute("totalActiveBorrows", activeBorrows.size());
            model.addAttribute("overdueCount", borrowRecordService.getOverdueBooks().size());
            
            System.out.println("Active borrows loaded: " + activeBorrows.size());
            
            return "active_borrows";
        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("errorMessage", "Error loading active borrows: " + e.getMessage());
            model.addAttribute("activeBorrows", List.of());
            model.addAttribute("totalActiveBorrows", 0);
            model.addAttribute("overdueCount", 0);
            return "active_borrows";
        }
    }
    
    // Return book
    @PostMapping("/return/{id}")
    public String returnBook(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            borrowRecordService.returnBook(id);
            redirectAttributes.addFlashAttribute("successMessage", "Book returned successfully!");
            System.out.println("Book returned successfully for borrow record ID: " + id);
        } catch (Exception e) {
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("errorMessage", "Error returning book: " + e.getMessage());
        }
        return "redirect:/borrows/active";
    }
    
    // Show overdue books
    @GetMapping("/overdue")
    public String showOverdueBooks(Model model) {
        try {
            List<BorrowRecord> overdueBooks = borrowRecordService.getOverdueBooks();
            model.addAttribute("overdueBooks", overdueBooks);
            model.addAttribute("overdueCount", overdueBooks.size());
            return "overdue_books";
        } catch (Exception e) {
            model.addAttribute("errorMessage", "Error loading overdue books: " + e.getMessage());
            return "overdue_books";
        }
    }
    
    // Show student borrowing history
    @GetMapping("/history/{studentId}")
    public String showStudentHistory(@PathVariable Long studentId, Model model) {
        try {
            model.addAttribute("student", studentService.findById(studentId));
            model.addAttribute("borrowHistory", borrowRecordService.getBorrowHistoryByStudent(studentId));
            model.addAttribute("activeBorrows", borrowRecordService.getActiveBorrowsByStudent(studentId));
            model.addAttribute("remainingLimit", borrowRecordService.getRemainingBorrowLimit(studentId));
            return "borrow_history";
        } catch (Exception e) {
            return "redirect:/students";
        }
    }
    
    // Show all borrow history
    @GetMapping("/history")
    public String showAllBorrowHistory(Model model) {
        try {
            List<BorrowRecord> allBorrows = borrowRecordService.getAllBorrowRecords();
            model.addAttribute("allBorrows", allBorrows);
            model.addAttribute("totalBorrows", borrowRecordService.getTotalBorrows());
            model.addAttribute("activeBorrows", borrowRecordService.getActiveBorrowsCount());
            model.addAttribute("returnedBorrows", borrowRecordService.getReturnedBorrowsCount());
            return "all_borrow_history";
        } catch (Exception e) {
            model.addAttribute("errorMessage", "Error loading borrow history: " + e.getMessage());
            return "all_borrow_history";
        }
    }
}