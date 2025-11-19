package com.congress.event.controller;

import com.congress.event.dto.*;
import com.congress.event.model.User;
import com.congress.event.model.VerificationToken;
import com.congress.event.security.JwtUtil;
import com.congress.event.service.CustomUserDetailsService;
import com.congress.event.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private CustomUserDetailsService userDetailsService;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private UserService userService;

    // ---------------- Register ----------------
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest request) {
        User createdUser = userService.registerUser(
                request.getUsername(),
                request.getEmail(),
                request.getPassword()
        );

        return ResponseEntity.ok(
                Map.of(
                        "message", "Inscription réussie. Vérifiez votre email pour activer le compte.",
                        "user", Map.of(
                                "username", createdUser.getUsername(),
                                "email", createdUser.getEmail(),
                                "role", createdUser.getRole()
                        )
                )
        );
    }

    // ---------------- Verify Account ----------------
    @GetMapping("/verify")
    public ResponseEntity<String> verify(@RequestParam String token) {
        String response = userService.verifyUser(token);

        if (response.equals("Votre compte est activé !")) {
            String html = "<!DOCTYPE html>" +
                    "<html>" +
                    "<head><title>Activated account</title></head>" +
                    "<body>" +
                    "<h1>Your account is activated !</h1>" +
                    "</body>" +
                    "</html>";
            return ResponseEntity.ok().header("Content-Type", "text/html").body(html);
        } else {
            String html = "<!DOCTYPE html>" +
                    "<html>" +
                    "<head><title>Erreur</title></head>" +
                    "<body>" +
                    "<h1>Erreur :</h1>" +
                    "<p>" + response + "</p>" +
                    "</body>" +
                    "</html>";
            return ResponseEntity.badRequest().header("Content-Type", "text/html").body(html);
        }
    }


    // ---------------- Login ----------------
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getEmail(), // use email here
                            request.getPassword()
                    )
            );
        } catch (DisabledException e) {
            return ResponseEntity.status(403)
                    .body("Compte non activé. Vérifiez votre email.");
        } catch (BadCredentialsException e) {
            return ResponseEntity.status(401)
                    .body("Identifiants invalides");
        }

        UserDetails userDetails = userDetailsService.loadUserByUsername(request.getEmail());
        String jwt = jwtUtil.generateToken(userDetails);
        User user = userService.findByEmail(request.getEmail());

        return ResponseEntity.ok(
                Map.of(
                        "token", jwt,
                        "username", user.getUsername(),
                        "email", user.getEmail(),
                        "role", user.getRole()
                )
        );
    }

    // ---------------- Forgot Password ----------------
    @PostMapping("/forgot-password")
    public ResponseEntity<?> forgotPassword(@RequestBody ForgotPasswordRequest request) {
        String response = userService.forgotPassword(request.getEmail());
        return ResponseEntity.ok(Map.of("message", response));
    }

    // ---------------- Reset Password ----------------
    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@RequestBody ResetPasswordRequest request) {
        String response = userService.resetPassword(request.getToken(), request.getNewPassword());
        return ResponseEntity.ok(Map.of("message", response));
    }
}
