package com.swna.server.auth.jwt;

import java.io.IOException;
import java.util.List;

import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.security.core.Authentication;

import com.swna.server.common.exception.BusinessException;
import com.swna.server.common.exception.ErrorCode;
import com.swna.server.user.security.UserPrincipal;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {

    private final JwtProvider jwtProvider;

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest req,
                                    @NonNull HttpServletResponse res,
                                    @NonNull FilterChain chain) throws ServletException, IOException {

        try {
            String token = resolveToken(req);

            if (token != null) {

                // 1️⃣ JWT에서 데이터 추출
                Long userId = jwtProvider.getUserId(token);
                String role = jwtProvider.getRole(token);

                // 2️⃣ UserPrincipal 생성 (핵심 ⭐)
                UserPrincipal principal = new UserPrincipal(userId, role);

                // 3️⃣ 권한 생성
                var authorities = List.of(new SimpleGrantedAuthority("ROLE_" + role));

                // 4️⃣ Authentication 생성 (principal = UserPrincipal)
                Authentication auth =
                        new UsernamePasswordAuthenticationToken(
                                principal,
                                null,
                                authorities
                        );

                // 5️⃣ SecurityContext에 저장
                SecurityContextHolder.getContext().setAuthentication(auth);
            }
            
            chain.doFilter(req, res);
        } catch (BusinessException e) {
            // 예외는 컨트롤러 어드바이스로 넘김
            handleException(res, e);
        }

    }

    @Override
    protected boolean shouldNotFilter(@NonNull HttpServletRequest request) throws ServletException {
        String path = request.getServletPath();
        // 로그인, 재발급, 회원가입 등은 토큰 검사를 건너뜁니다.
        return path.startsWith("/auth/");
    }

    private String resolveToken(HttpServletRequest request) {
        String bearer = request.getHeader("Authorization");

        return (bearer != null && bearer.startsWith("Bearer "))
                ? bearer.substring(7)
                : null;
    }

    private void handleException(HttpServletResponse res, BusinessException e) throws IOException {

        ErrorCode errorCode = e.getErrorCode();

        res.setStatus(errorCode.getStatus().value());
        res.setContentType("application/json;charset=UTF-8");

        res.getWriter().write("""
            {
            "success": false,
            "code": "%s",
            "message": "%s"
            }
        """.formatted(
                errorCode.getCode(),
                errorCode.getMessage()
        ));
    }
}
