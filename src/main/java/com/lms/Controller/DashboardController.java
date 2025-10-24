package com.lms.Controller;

import com.lms.Service.StudentService;
import com.lms.Service.BookService;
import com.lms.Service.BorrowRecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class DashboardController {
    
    @Autowired
    private StudentService studentService;
    
    @Autowired
    private BookService bookService;
    
    @Autowired
    private BorrowRecordService borrowRecordService;
    
    @GetMapping("/")
    public String dashboard(Model model) {
        try {
            // Get counts using existing service methods
            long studentCount = studentService.count();
            long bookCount = bookService.count();
            long availableBookCount = bookService.findAvailableBooks().size();
            long activeBorrowsCount = borrowRecordService.getActiveBorrowsCount();
            long overdueBooksCount = borrowRecordService.getOverdueBooks().size();
            
            // Add statistics to model
            model.addAttribute("studentCount", studentCount);
            model.addAttribute("bookCount", bookCount);
            model.addAttribute("availableBookCount", availableBookCount);
            model.addAttribute("activeBorrowsCount", activeBorrowsCount);
            model.addAttribute("overdueBooksCount", overdueBooksCount);
            
            return "dashboard"; // Changed to match your actual template name
        } catch (Exception e) {
            // If no data, show zeros
            model.addAttribute("studentCount", 0);
            model.addAttribute("bookCount", 0);
            model.addAttribute("availableBookCount", 0);
            model.addAttribute("activeBorrowsCount", 0);
            model.addAttribute("overdueBooksCount", 0);
            return "dashboard"; // Changed to match your actual template name
        }
    }
}