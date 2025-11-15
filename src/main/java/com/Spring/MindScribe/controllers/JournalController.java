package com.Spring.MindScribe.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.Spring.MindScribe.models.User;
import com.Spring.MindScribe.repository.UserRepository;

@RestController
@RequestMapping("/api/v1")
public class JournalController {

    @Autowired
    private UserRepository userRepository;
    
    @GetMapping("/hello")
    public ResponseEntity<String> sayHello() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        User user = userRepository.findByEmail(email).orElseThrow();
        return ResponseEntity.ok("Hello, " + user.getName() + "! Your email is: " + user.getEmail());
    }
}
