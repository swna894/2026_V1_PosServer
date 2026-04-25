package com.swna.server.user.infrastructure.security;

import org.springframework.security.core.context.SecurityContextHolder;

import com.swna.server.user.security.UserPrincipal;

public class SecurityUtils {

    public static Long getCurrentUserId() {

        var auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth == null || !(auth.getPrincipal() instanceof UserPrincipal principal)) {
            throw new RuntimeException("Unauthorized");
        }

        return principal.getUserId();
    }
}
