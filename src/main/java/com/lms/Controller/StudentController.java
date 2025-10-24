package com.lms.Controller;

import com.lms.Entity.Student;
import com.lms.Service.StudentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/students")
public class StudentController {
    
    @Autowired
    private StudentService studentService;
    
    @GetMapping
    public String listStudents(Model model) {
        try {
            List<Student> students = studentService.getAllStudents();
            model.addAttribute("students", students);
            return "students";
        } catch (Exception e) {
            model.addAttribute("errorMessage", "Error loading students: " + e.getMessage());
            return "students";
        }
    }
    
    @GetMapping("/new")
    public String showStudentForm(Model model) {
        model.addAttribute("student", new Student());
        return "student_form";
    }
    
    @PostMapping("/save")
    public String saveStudent(@ModelAttribute Student student, RedirectAttributes redirectAttributes) {
        try {
            // Check if email already exists
            if (studentService.isEmailExists(student.getEmail())) {
                redirectAttributes.addFlashAttribute("errorMessage", "Error: Email already exists!");
                redirectAttributes.addFlashAttribute("student", student);
                return "redirect:/students/new";
            }
            
            // Check if student ID already exists
            if (studentService.isStudentIdExists(student.getStudentId())) {
                redirectAttributes.addFlashAttribute("errorMessage", "Error: Student ID already exists!");
                redirectAttributes.addFlashAttribute("student", student);
                return "redirect:/students/new";
            }
            
            studentService.saveStudent(student);
            redirectAttributes.addFlashAttribute("successMessage", "Student added successfully!");
            return "redirect:/students";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error: " + e.getMessage());
            redirectAttributes.addFlashAttribute("student", student);
            return "redirect:/students/new";
        }
    }
    
    @GetMapping("/edit/{id}")
    public String editStudent(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        try {
            Optional<Student> student = studentService.getStudentById(id);
            if (student.isPresent()) {
                model.addAttribute("student", student.get());
                return "student_form";
            } else {
                redirectAttributes.addFlashAttribute("errorMessage", "Student not found with id: " + id);
                return "redirect:/students";
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error: " + e.getMessage());
            return "redirect:/students";
        }
    }
    
    @PostMapping("/update/{id}")
    public String updateStudent(@PathVariable Long id, @ModelAttribute Student student, 
                               RedirectAttributes redirectAttributes) {
        try {
            // Check if email already exists for other students
            Optional<Student> existingStudent = studentService.getStudentById(id);
            if (existingStudent.isPresent()) {
                Student currentStudent = existingStudent.get();
                if (!currentStudent.getEmail().equals(student.getEmail()) && 
                    studentService.isEmailExists(student.getEmail())) {
                    redirectAttributes.addFlashAttribute("errorMessage", "Error: Email already exists!");
                    return "redirect:/students/edit/" + id;
                }
                
                if (!currentStudent.getStudentId().equals(student.getStudentId()) && 
                    studentService.isStudentIdExists(student.getStudentId())) {
                    redirectAttributes.addFlashAttribute("errorMessage", "Error: Student ID already exists!");
                    return "redirect:/students/edit/" + id;
                }
            }
            
            studentService.updateStudent(id, student);
            redirectAttributes.addFlashAttribute("successMessage", "Student updated successfully!");
            return "redirect:/students";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error: " + e.getMessage());
            return "redirect:/students/edit/" + id;
        }
    }
    
    @GetMapping("/delete/{id}")
    public String deleteStudent(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            studentService.deleteStudent(id);
            redirectAttributes.addFlashAttribute("successMessage", "Student deleted successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error: " + e.getMessage());
        }
        return "redirect:/students";
    }
    
    @GetMapping("/search")
    public String searchStudents(@RequestParam String keyword, Model model) {
        try {
            List<Student> students = studentService.searchStudents(keyword);
            model.addAttribute("students", students);
            model.addAttribute("searchKeyword", keyword);
            return "students";
        } catch (Exception e) {
            model.addAttribute("errorMessage", "Error searching students: " + e.getMessage());
            return "students";
        }
    }
}