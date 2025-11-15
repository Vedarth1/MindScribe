package com.Spring.MindScribe.services;

import java.util.Optional;

import org.springframework.security.core.AuthenticationException;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.Spring.MindScribe.dto.ResponseDTO;
import com.Spring.MindScribe.dto.UserDTO;
import com.Spring.MindScribe.models.User;
import com.Spring.MindScribe.repository.UserRepository;
import com.Spring.MindScribe.utils.JwtService;

@Service
public class AuthenticationService {
    
    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final PasswordEncoder passwordEncoder;

    public AuthenticationService(UserRepository userRepository, 
                                 JwtService jwtService, 
                                 AuthenticationManager authenticationManager,
                                 PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.jwtService = jwtService;
        this.authenticationManager = authenticationManager;
        this.passwordEncoder = passwordEncoder;
    }

    public ResponseEntity<ResponseDTO> signup(UserDTO userDTO) {
        Optional<User> existingUser = userRepository.findByEmail(userDTO.getEmail());
        if (existingUser.isPresent()) {
            return ResponseEntity.badRequest().body(new ResponseDTO(null,"User already exists"));
        }

        if (!userDTO.getPassword().equals(userDTO.getConfirmPassword())) {
            return ResponseEntity.badRequest().body(new ResponseDTO(null,"Passwords do not match"));
        }

        User user = new User();
        user.setName(userDTO.getName());
        user.setEmail(userDTO.getEmail());
        user.setPassword(passwordEncoder.encode(userDTO.getPassword()));

        userRepository.save(user);

        return ResponseEntity.ok(new ResponseDTO(null,"User registered successfully"));
    }

    public ResponseEntity<ResponseDTO> login(String email, String password) {
        try {
            
            Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(email, password)
            );
            
            SecurityContextHolder.getContext().setAuthentication(authentication);
            String token = jwtService.generateToken(authentication);
            
            return ResponseEntity.status(200).body(new ResponseDTO(token,"Logged In Successfully"));
        }
        catch(AuthenticationException e)
        {
            return ResponseEntity.status(401).body(new ResponseDTO(null, "Invalid email or password!"));
        }
    }
}
