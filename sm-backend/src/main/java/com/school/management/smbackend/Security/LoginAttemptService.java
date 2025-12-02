package com.school.management.smbackend.Security;

import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

@Component
public class LoginAttemptService {
    private static final int MAX_ATTEMPTS = 5;
    private static final long LOCK_TIME_DURATION = 60000; // 1 minute in milliseconds

    private final Map<String, AttemptInfo> attemptsCache = new ConcurrentHashMap<>();

    public void loginSucceeded(String key) {
        attemptsCache.remove(key);
    }

    public void loginFailed(String key) {
        AttemptInfo attemptInfo = attemptsCache.computeIfAbsent(key, k -> new AttemptInfo());
        attemptInfo.incrementAttempts();
        attemptInfo.setLastAttemptTime(System.currentTimeMillis());
    }

    public boolean isBlocked(String key) {
        AttemptInfo attemptInfo = attemptsCache.get(key);
        if (attemptInfo == null) {
            return false;
        }

        long currentTime = System.currentTimeMillis();
        long timeSinceLastAttempt = currentTime - attemptInfo.getLastAttemptTime();

        // Reset if lock time has passed
        if (timeSinceLastAttempt > LOCK_TIME_DURATION) {
            attemptsCache.remove(key);
            return false;
        }

        // Block if max attempts exceeded and still within lock time
        return attemptInfo.getAttempts() >= MAX_ATTEMPTS;
    }

    public long getRemainingLockTime(String key) {
        AttemptInfo attemptInfo = attemptsCache.get(key);
        if (attemptInfo == null) {
            return 0;
        }
        
        long currentTime = System.currentTimeMillis();
        long timeSinceLastAttempt = currentTime - attemptInfo.getLastAttemptTime();
        long remaining = LOCK_TIME_DURATION - timeSinceLastAttempt;
        
        return remaining > 0 ? remaining / 1000 : 0; // Return seconds
    }

    private static class AttemptInfo {
        private final AtomicInteger attempts = new AtomicInteger(0);
        private long lastAttemptTime;

        public int incrementAttempts() {
            return attempts.incrementAndGet();
        }

        public int getAttempts() {
            return attempts.get();
        }

        public long getLastAttemptTime() {
            return lastAttemptTime;
        }

        public void setLastAttemptTime(long lastAttemptTime) {
            this.lastAttemptTime = lastAttemptTime;
        }
    }
}
