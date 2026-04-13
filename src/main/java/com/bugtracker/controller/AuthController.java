package com.bugtracker.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import com.bugtracker.entity.User;
import com.bugtracker.repository.UserRepository;

@Controller
public class AuthController {
	  @Autowired
	   private UserRepository userRepository;
    @GetMapping("/login")
    public String loginPage() {
        return "login";
    }

    @GetMapping("/register")
    public String registerPage() {
        return "register";
    }
    
    @PostMapping("/register")
    public String registerUser(User user) {
        userRepository.save(user);
        return "redirect:/login";
    }
    
    @GetMapping("/")
    public String home() {

        return "redirect:/login";

    }
}