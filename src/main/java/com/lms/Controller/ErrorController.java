package com.lms.Controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class ErrorController {
    
    @GetMapping("/error")
    public String showErrorPage(Model model) {
        return "error";
    }
}