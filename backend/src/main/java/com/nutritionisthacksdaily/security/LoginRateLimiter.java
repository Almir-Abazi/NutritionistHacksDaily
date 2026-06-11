package com.nutritionisthacksdaily.security;

import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class LoginRateLimiter {

    private static final int MAX_ATTEMPTS = 5;
    private static final Duration BLOCK_DURATION = Duration.ofMinutes(15);
    private static final Duration WINDOW = Duration.ofMinutes(10);

    private final ConcurrentHashMap<String, LoginAttempts> attemptsByIp = new ConcurrentHashMap<>();

    public boolean isBlocked(String ip) {
        LoginAttempts entry = attemptsByIp.get(ip);
        if (entry == null) {
            return false;
        }
        LocalDateTime now = LocalDateTime.now();
        synchronized (entry) {
            if (entry.blockedUntil != null && now.isBefore(entry.blockedUntil)) {
                return true;
            }
            if (now.isAfter(entry.firstAttemptTime.plus(WINDOW))) {
                attemptsByIp.remove(ip);
            }
            return false;
        }
    }

    public void recordFailedAttempt(String ip) {
        LocalDateTime now = LocalDateTime.now();
        LoginAttempts entry = attemptsByIp.computeIfAbsent(ip, key -> new LoginAttempts(now));
        synchronized (entry) {
            entry.attempts++;
            if (entry.attempts >= MAX_ATTEMPTS) {
                entry.blockedUntil = now.plus(BLOCK_DURATION);
            }
        }
    }

    public void resetAttempts(String ip) {
        attemptsByIp.remove(ip);
    }

    private static class LoginAttempts {
        int attempts;
        final LocalDateTime firstAttemptTime;
        LocalDateTime blockedUntil;

        LoginAttempts(LocalDateTime firstAttemptTime) {
            this.firstAttemptTime = firstAttemptTime;
        }
    }
}
