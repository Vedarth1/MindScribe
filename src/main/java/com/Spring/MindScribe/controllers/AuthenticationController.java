package com.Spring.MindScribe.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.Spring.MindScribe.dto.RequestDTO;
import com.Spring.MindScribe.dto.ResponseDTO;
import com.Spring.MindScribe.dto.UserDTO;
import com.Spring.MindScribe.services.AuthenticationService;

@RestController
@RequestMapping("/api/auth")
public class AuthenticationController {

    @Autowired
    private AuthenticationService authService;

    @PostMapping("/login")
    public ResponseEntity<ResponseDTO> login(@RequestBody RequestDTO request) {
        return authService.login(request.getEmail(),request.getPassword());
    }

    @PostMapping("/signup")
    public ResponseEntity<ResponseDTO> signup(@RequestBody UserDTO user) {
        return authService.signup(user);
    }
}
