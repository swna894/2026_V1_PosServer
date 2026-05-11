package com.swna.server.auth.jwt;

import java.util.Date;

import org.springframework.stereotype.Component;

import com.swna.server.common.config.JwtProperties;
import com.swna.server.common.exception.ExceptionUtils;
import com.swna.server.user.entity.model.Role;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class JwtProvider {

    private final JwtProperties jwtProperties;

    // 🔥 Access Token
    public String createAccessToken(Long userId, Role role) {
        return createToken(userId, role, jwtProperties.getAccessExp());
    }

    // 🔥 Refresh Token
    public String createRefreshToken(Long userId, Role role) {
        return createToken(userId, role, jwtProperties.getRefreshExp());
    }

    private String createToken(Long userId, Role role, long exp) {
        return Jwts.builder()
                .setSubject(String.valueOf(userId))
                .claim("role", role.name())
                .setExpiration(new Date(System.currentTimeMillis() + exp))
                .signWith(Keys.hmacShaKeyFor(jwtProperties.getSecret().getBytes()))
                .compact();
    }


    public Claims parseClaims(String token) {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(jwtProperties.getSecret().getBytes())
                    .build()
                    .parseClaimsJws(token)
                    .getBody();

        } catch (ExpiredJwtException e) {
            throw ExceptionUtils.tokenExpired();
        } catch (JwtException | IllegalArgumentException e) {
            throw ExceptionUtils.invalidToken();
        }
    }

    public long getUserId(String token) {
        // parseLong은 long을 반환하므로 타입 변환 경고가 사라집니다.
        return Long.parseLong(parseClaims(token).getSubject());
    }

    public String getRole(String token) {
        return parseClaims(token).get("role", String.class);
    }
}
