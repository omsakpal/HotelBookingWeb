package com.hotel.hotelbooking.service;

import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

@Service
public class OtpService {

    private final Map<String, String> otpStorage = new HashMap<>();
    private final Map<String, Long> otpExpiry = new HashMap<>();
    private final Map<String, Long> resendTime = new HashMap<>();

    public String generateOtp(String email) {
        String otp = String.format("%06d", new Random().nextInt(1000000));

        otpStorage.put(email, otp);
        otpExpiry.put(email, System.currentTimeMillis() + 5 * 60 * 1000);
        resendTime.put(email, System.currentTimeMillis());

        return otp;
    }

    public boolean canResend(String email) {
        Long lastSent = resendTime.get(email);

        if (lastSent == null) {
            return true;
        }

        return System.currentTimeMillis() - lastSent >= 60 * 1000;
    }

    public long getRemainingResendSeconds(String email) {
        Long lastSent = resendTime.get(email);

        if (lastSent == null) {
            return 0;
        }

        long remaining = 60 - (System.currentTimeMillis() - lastSent) / 1000;

        return Math.max(0, remaining);
    }

    public boolean verifyOtp(String email, String otp) {
        String savedOtp = otpStorage.get(email);
        Long expiry = otpExpiry.get(email);

        if (savedOtp == null || expiry == null) {
            return false;
        }

        if (System.currentTimeMillis() > expiry) {
            otpStorage.remove(email);
            otpExpiry.remove(email);
            resendTime.remove(email);
            return false;
        }

        if (savedOtp.equals(otp)) {
            otpStorage.remove(email);
            otpExpiry.remove(email);
            resendTime.remove(email);
            return true;
        }

        return false;
    }
}