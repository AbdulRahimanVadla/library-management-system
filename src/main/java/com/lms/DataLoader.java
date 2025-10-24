package com.lms;

import com.lms.Entity.Student;
import com.lms.Entity.Book;
import com.lms.Repository.StudentRepository;
import com.lms.Repository.BookRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class DataLoader implements CommandLineRunner {

    @Autowired
    private StudentRepository studentRepository;
    
    @Autowired
    private BookRepository bookRepository;

    @Override
    public void run(String... args) throws Exception {
        // Only load data if database is empty
        if (studentRepository.count() == 0) {
            loadStudentData();
        }
        
        if (bookRepository.count() == 0) {
            loadBookData();
        }
    }

    private void loadStudentData() {
        Student student1 = new Student("John Doe", "john.doe@example.com", "STU001");
        Student student2 = new Student("Jane Smith", "jane.smith@example.com", "STU002");
        Student student3 = new Student("Mike Johnson", "mike.johnson@example.com", "STU003");
        Student student4 = new Student("Sarah Wilson", "sarah.wilson@example.com", "STU004");
        
        studentRepository.save(student1);
        studentRepository.save(student2);
        studentRepository.save(student3);
        studentRepository.save(student4);
        
        System.out.println("Sample students loaded successfully!");
    }

    private void loadBookData() {
        Book book1 = new Book("The Great Gatsby", "F. Scott Fitzgerald", "9780743273565", 1925, "Fiction");
        Book book2 = new Book("To Kill a Mockingbird", "Harper Lee", "9780061120084", 1960, "Fiction");
        Book book3 = new Book("1984", "George Orwell", "9780451524935", 1949, "Science Fiction");
        Book book4 = new Book("Pride and Prejudice", "Jane Austen", "9780141439518", 1813, "Romance");
        Book book5 = new Book("The Hobbit", "J.R.R. Tolkien", "9780547928227", 1937, "Fantasy");
        Book book6 = new Book("Harry Potter and the Sorcerer's Stone", "J.K. Rowling", "9780590353427", 1997, "Fantasy");
        
        // FIXED: Changed from setAvailable(false) to setIsAvailable(false)
        book6.setIsAvailable(false);
        
        bookRepository.save(book1);
        bookRepository.save(book2);
        bookRepository.save(book3);
        bookRepository.save(book4);
        bookRepository.save(book5);
        bookRepository.save(book6);
        
        System.out.println("Sample books loaded successfully!");
    }
}