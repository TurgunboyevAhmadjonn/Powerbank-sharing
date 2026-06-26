package com.anor.userservice.controller;

import com.anor.userservice.dto.AuthResponse;
import com.anor.userservice.dto.PhoneRequest;
import com.anor.userservice.dto.VerifyRequest;
import com.anor.userservice.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    // POST /auth/phone
    @PostMapping("/phone")
    public ResponseEntity<AuthResponse> requestOtp(@RequestBody PhoneRequest request) {
        authService.sendOtp(request.getPhone());
        return ResponseEntity.ok(new AuthResponse(null, "OTP sent successfully"));
    }

    // POST /auth/verify
    @PostMapping("/verify")
    public ResponseEntity<AuthResponse> verifyOtp(@RequestBody VerifyRequest request) {
        String token = authService.verifyOtp(request.getPhone(), request.getOtpCode());
        return ResponseEntity.ok(new AuthResponse(token, "Login successful"));
    }
}
