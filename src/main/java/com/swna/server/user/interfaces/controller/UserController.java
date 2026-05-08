package com.swna.server.user.interfaces.controller;

import org.springframework.lang.NonNull;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.swna.server.user.application.usecase.CreateUserUseCase;
import com.swna.server.user.application.usecase.GetUserUseCase;
import com.swna.server.user.application.usecase.SignupUseCase;
import com.swna.server.user.dto.CreateUserRequest;
import com.swna.server.user.dto.SignupRequest;
import com.swna.server.user.dto.UserResponse;
import com.swna.server.user.security.UserPrincipal;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final CreateUserUseCase createUserUseCase;
    private final GetUserUseCase getUserUseCase;
    private final SignupUseCase signupUseCase;

    @PostMapping
    public UserResponse create(@RequestBody CreateUserRequest req) {
        return createUserUseCase.execute(req);
    }

    @GetMapping("/{id}")
    public UserResponse get(@NonNull @PathVariable Long id) {
        return getUserUseCase.execute(id);
    }

    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    @GetMapping("/me")
    public String me() {
        return "OK";
    }

    @PostMapping("/signup")
    public void signup(@RequestBody SignupRequest req) {
        signupUseCase.execute(req.email(), req.password());
    }



    @GetMapping("/me2")
    public String me2(@AuthenticationPrincipal UserPrincipal user) {
        return "userId = " + user.getUserId();
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/user/{id}")
    public String deleteUser(@PathVariable(value = "id") Long id) {
        return "OK";
    }

    @PostAuthorize("returnObject == authentication.principal.userId")
    @GetMapping("/user/{id}")
    public String getUser(@PathVariable(value = "id") Long id) {
        return "OK";
    }
}