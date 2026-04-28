package com.bookmyshow.controller;

import com.bookmyshow.dto.ApiResponse;
import com.bookmyshow.dto.JwtResponse;
import com.bookmyshow.dto.LoginRequest;
import com.bookmyshow.dto.RegisterRequest;
import com.bookmyshow.model.User;
import com.bookmyshow.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<ApiResponse> register(@Valid @RequestBody RegisterRequest request) {
        User user = authService.register(request);
        return ResponseEntity.ok(new ApiResponse(true, "User registered successfully",
                java.util.Map.of("id", user.getId(), "email", user.getEmail(), "name", user.getName())));
    }

    @PostMapping("/login")
    public ResponseEntity<JwtResponse> login(@Valid @RequestBody LoginRequest request) {
        JwtResponse response = authService.login(request);
        return ResponseEntity.ok(response);
    }
}
