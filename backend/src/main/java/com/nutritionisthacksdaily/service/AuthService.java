package com.nutritionisthacksdaily.service;

import com.nutritionisthacksdaily.dto.AuthRequest;
import com.nutritionisthacksdaily.dto.AuthResponse;
import com.nutritionisthacksdaily.entity.AdminUser;
import com.nutritionisthacksdaily.exception.RateLimitException;
import com.nutritionisthacksdaily.repository.AdminUserRepository;
import com.nutritionisthacksdaily.security.JwtUtil;
import com.nutritionisthacksdaily.security.LoginRateLimiter;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final AdminUserRepository adminUserRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final LoginRateLimiter loginRateLimiter;

    public AuthService(AdminUserRepository adminUserRepository,
                       PasswordEncoder passwordEncoder,
                       JwtUtil jwtUtil,
                       LoginRateLimiter loginRateLimiter) {
        this.adminUserRepository = adminUserRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
        this.loginRateLimiter = loginRateLimiter;
    }

    public AuthResponse login(AuthRequest request, HttpServletRequest httpRequest) {
        String ip = resolveClientIp(httpRequest);

        if (loginRateLimiter.isBlocked(ip)) {
            throw new RateLimitException("Shumë përpjekje. Provo pas 15 minutash.");
        }

        AdminUser user = adminUserRepository.findByUsername(request.getUsername())
                .orElse(null);

        if (user == null || !passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
            loginRateLimiter.recordFailedAttempt(ip);
            throw new BadCredentialsException("Invalid username or password");
        }

        loginRateLimiter.resetAttempts(ip);
        return new AuthResponse(jwtUtil.generateToken(user.getUsername()));
    }

    /**
     * In production the backend sits behind nginx, so the direct remote address
     * is the proxy's. Prefer the forwarded headers nginx sets (X-Real-IP).
     */
    private String resolveClientIp(HttpServletRequest request) {
        String forwarded = request.getHeader("X-Forwarded-For");
        if (forwarded != null && !forwarded.isBlank()) {
            return forwarded.split(",")[0].trim();
        }
        String realIp = request.getHeader("X-Real-IP");
        if (realIp != null && !realIp.isBlank()) {
            return realIp.trim();
        }
        return request.getRemoteAddr();
    }
}
