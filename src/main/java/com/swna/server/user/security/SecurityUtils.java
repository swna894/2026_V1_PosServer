package com.swna.server.user.security;

import org.springframework.security.core.context.SecurityContextHolder;

public class SecurityUtils {

    private SecurityUtils() {
    }

    public static Long getCurrentUserId() {

        var auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth == null || !(auth.getPrincipal() instanceof UserPrincipal principal)) {
            throw new RuntimeException("Unauthorized");
        }

        return principal.getUserId();
    }
}
