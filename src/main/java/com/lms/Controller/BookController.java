package com.lms.Controller;

import com.lms.Entity.Book;
import com.lms.Service.BookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/books")
public class BookController {
    
    @Autowired
    private BookService bookService;
    
    @GetMapping
    public String listBooks(Model model) {
        try {
            List<Book> books = bookService.findAll();
            model.addAttribute("books", books);
            return "books";
        } catch (Exception e) {
            model.addAttribute("errorMessage", "Error loading books: " + e.getMessage());
            return "books";
        }
    }
    
    @GetMapping("/new")
    public String showBookForm(Model model) {
        model.addAttribute("book", new Book());
        return "book_form";
    }
    
    @PostMapping("/save")
    public String saveBook(@ModelAttribute Book book, RedirectAttributes redirectAttributes) {
        try {
            // FIXED: Ensure availability is set
            if (book.getIsAvailable() == null) {
                book.setIsAvailable(true);
            }
            bookService.save(book);
            redirectAttributes.addFlashAttribute("successMessage", "Book added successfully!");
            return "redirect:/books";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error: " + e.getMessage());
            redirectAttributes.addFlashAttribute("book", book);
            return "redirect:/books/new";
        }
    }
    
    @GetMapping("/edit/{id}")
    public String editBook(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        try {
            Book book = bookService.findById(id);
            model.addAttribute("book", book);
            return "book_form";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error: " + e.getMessage());
            return "redirect:/books";
        }
    }
    
    @PostMapping("/update/{id}")
    public String updateBook(@PathVariable Long id, @ModelAttribute Book book, 
                            RedirectAttributes redirectAttributes) {
        try {
            book.setId(id);
            // FIXED: Ensure availability is set
            if (book.getIsAvailable() == null) {
                book.setIsAvailable(true);
            }
            bookService.save(book);
            redirectAttributes.addFlashAttribute("successMessage", "Book updated successfully!");
            return "redirect:/books";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error: " + e.getMessage());
            return "redirect:/books/edit/" + id;
        }
    }
    
    @GetMapping("/delete/{id}")
    public String deleteBook(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            bookService.deleteById(id);
            redirectAttributes.addFlashAttribute("successMessage", "Book deleted successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error: " + e.getMessage());
        }
        return "redirect:/books";
    }
    
    @GetMapping("/available")
    public String showAvailableBooks(Model model) {
        try {
            List<Book> availableBooks = bookService.findAvailableBooks();
            model.addAttribute("books", availableBooks);
            model.addAttribute("showOnlyAvailable", true);
            return "books";
        } catch (Exception e) {
            model.addAttribute("errorMessage", "Error loading available books: " + e.getMessage());
            return "books";
        }
    }
    
    @GetMapping("/toggle-availability/{id}")
    public String toggleBookAvailability(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            Book book = bookService.findById(id);
            book.setIsAvailable(!book.isAvailable());
            bookService.save(book);
            redirectAttributes.addFlashAttribute("successMessage", "Book availability updated!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error: " + e.getMessage());
        }
        return "redirect:/books";
    }
    
    @GetMapping("/search")
    public String searchBooks(@RequestParam(required = false) String title,
                             @RequestParam(required = false) String author,
                             @RequestParam(required = false) String genre,
                             Model model) {
        try {
            List<Book> books;
            
            if (title != null && !title.isEmpty()) {
                books = bookService.findByTitleContaining(title);
            } else if (author != null && !author.isEmpty()) {
                books = bookService.findByAuthorContaining(author);
            } else if (genre != null && !genre.isEmpty()) {
                books = bookService.findByGenre(genre);
            } else {
                books = bookService.findAll();
            }
            
            model.addAttribute("books", books);
            model.addAttribute("searchTitle", title);
            model.addAttribute("searchAuthor", author);
            model.addAttribute("searchGenre", genre);
            return "books";
        } catch (Exception e) {
            model.addAttribute("errorMessage", "Error searching books: " + e.getMessage());
            return "books";
        }
    }
}