package com.example.bankcards.util;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class EncryptionUtil {
    
    private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    
    public String encryptPassword(String rawPassword) {
        return passwordEncoder.encode(rawPassword);
    }
    
    public boolean matchesPassword(String rawPassword, String encodedPassword) {
        return passwordEncoder.matches(rawPassword, encodedPassword);
    }
    
    public String maskCardNumber(String cardNumber) {
        if (cardNumber == null || cardNumber.length() < 4) {
            return "**** **** **** ****";
        }
        String lastFour = cardNumber.substring(cardNumber.length() - 4);
        return "**** **** **** " + lastFour;
    }
    
    public String encryptCardNumber(String cardNumber) {
        // Simple encryption for demo purposes
        // In production, use proper encryption like AES
        StringBuilder encrypted = new StringBuilder();
        for (char c : cardNumber.toCharArray()) {
            encrypted.append((char) (c + 3));
        }
        return encrypted.toString();
    }
    
    public String decryptCardNumber(String encryptedCardNumber) {
        // Simple decryption for demo purposes
        // In production, use proper decryption like AES
        StringBuilder decrypted = new StringBuilder();
        for (char c : encryptedCardNumber.toCharArray()) {
            decrypted.append((char) (c - 3));
        }
        return decrypted.toString();
    }
}
