package com.mta.studytaskmanager.modules.auth.controller;

import com.mta.studytaskmanager.core.api.ApiResponse;
import com.mta.studytaskmanager.modules.auth.dto.AuthResponse;
import com.mta.studytaskmanager.modules.auth.dto.LoginRequest;
import com.mta.studytaskmanager.modules.auth.dto.RegisterRequest;
import com.mta.studytaskmanager.modules.auth.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@Controller
public class AuthController {
    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    public ApiResponse<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
        AuthResponse reponse = authService.register(request);
        return ApiResponse.success(reponse, "User registered successfully");
    }


    @PostMapping("/login")
    public ApiResponse<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        AuthResponse reponse = authService.login(request);
        return ApiResponse.success(reponse, "User logged in successfully");

    }
}
