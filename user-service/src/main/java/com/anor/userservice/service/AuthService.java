package com.anor.userservice.service;

import com.anor.userservice.model.User;
import com.anor.userservice.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.Optional;
import java.util.Random;

@Service
public class AuthService {

    @Autowired
    private UserRepository userRepository;

    // Step 1: Generate OTP and save it
    public void sendOtp(String phone) {
        // Generate 6 digit OTP
        String otp = String.format("%06d", new Random().nextInt(999999));

        // Find existing user or create new one
        User user = userRepository.findByPhone(phone)
                .orElse(new User());

        user.setPhone(phone);
        user.setOtpCode(otp);
        user.setOtpExpiresAt(OffsetDateTime.now().plusMinutes(5));
        user.setStatus("PENDING");

        userRepository.save(user);

        // TODO: Send OTP via Telegram
        // For now just print it
        System.out.println("OTP for " + phone + " is: " + otp);
    }

    // Step 2: Verify OTP and return token
    public String verifyOtp(String phone, String otpCode) {
        Optional<User> optionalUser = userRepository.findByPhone(phone);

        if (optionalUser.isEmpty()) {
            throw new RuntimeException("User not found");
        }

        User user = optionalUser.get();

        // Check OTP matches
        if (!user.getOtpCode().equals(otpCode)) {
            throw new RuntimeException("Invalid OTP");
        }

        // Check OTP not expired
        if (user.getOtpExpiresAt().isBefore(OffsetDateTime.now())) {
            throw new RuntimeException("OTP expired");
        }

        // Mark user as active
        user.setStatus("ACTIVE");
        userRepository.save(user);

        // TODO: Return real JWT from Keycloak
        // For now return a fake token
        return "fake-jwt-token-" + user.getId();
    }
}
