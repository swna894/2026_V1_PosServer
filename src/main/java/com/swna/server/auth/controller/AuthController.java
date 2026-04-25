package com.swna.server.auth.controller;

import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.swna.server.auth.application.usecase.LoginUseCase;
import com.swna.server.auth.application.usecase.LogoutUseCase;
import com.swna.server.auth.application.usecase.ReissueTokenUseCase;
import com.swna.server.auth.dto.request.LoginRequest;
import com.swna.server.auth.dto.request.TokenRequest;
import com.swna.server.auth.dto.response.LoginResponse;
import com.swna.server.auth.dto.response.TokenResponse;
import com.swna.server.common.response.ApiResponse;
import com.swna.server.user.application.usecase.SignupUseCase;
import com.swna.server.user.security.UserPrincipal;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class AuthController {

    private final LoginUseCase loginUseCase;
    private final ReissueTokenUseCase reissueUseCase;
    private final LogoutUseCase logoutUseCase;
    private final SignupUseCase signupUseCase;

    @PostMapping("/login")
    public ApiResponse<LoginResponse> login(@RequestBody LoginRequest req) {
        System.err.println("LoginRequest: " + req.email() + ", " + req.password());
        return ApiResponse.success(loginUseCase.execute(req));
    }

    @PostMapping("/reissue")
    public ApiResponse<TokenResponse> reissue(@RequestBody TokenRequest req) {
        return ApiResponse.success(reissueUseCase.execute(req));
    }

    @PostMapping("/logout")
    public ApiResponse<Void> logout(Authentication auth) {
        Long userId = ((UserPrincipal) auth.getPrincipal()).getUserId();
        logoutUseCase.execute(userId);
        return ApiResponse.success(null);
    }

    @PostMapping("/signup")
    public ApiResponse<Void> signup(@RequestParam String email,
                                   @RequestParam String password) {
        signupUseCase.execute(email, password);
        return ApiResponse.success(null);
    }
}
