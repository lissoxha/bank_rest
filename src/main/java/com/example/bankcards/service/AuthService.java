package com.example.bankcards.service;

import com.example.bankcards.dto.AuthResponse;
import com.example.bankcards.dto.LoginRequest;
import com.example.bankcards.entity.User;
import com.example.bankcards.exception.BusinessException;
import com.example.bankcards.exception.UnauthorizedException;
import com.example.bankcards.util.EncryptionUtil;
import com.example.bankcards.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class AuthService implements UserDetailsService {
    
    @Autowired
    private UserService userService;
    
    @Autowired
    private EncryptionUtil encryptionUtil;
    
    @Autowired
    private JwtUtil jwtUtil;
    
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        try {
            return userService.findUserEntityByUsername(username);
        } catch (Exception e) {
            throw new UsernameNotFoundException("User not found with username: " + username);
        }
    }
    
    public AuthResponse authenticate(LoginRequest loginRequest) {
        try {
            User user = userService.findUserEntityByUsername(loginRequest.getUsername());
            
            if (!user.getIsActive()) {
                throw new UnauthorizedException("Account is deactivated");
            }
            
            if (!encryptionUtil.matchesPassword(loginRequest.getPassword(), user.getPassword())) {
                throw new UnauthorizedException("Invalid credentials");
            }
            
            String token = jwtUtil.generateToken(user.getUsername(), user.getRole().name());
            
            return new AuthResponse(token, user.getUsername(), user.getRole().name());
        } catch (Exception e) {
            if (e instanceof UnauthorizedException) {
                throw e;
            }
            throw new UnauthorizedException("Authentication failed");
        }
    }
    
    public boolean validateToken(String token) {
        return jwtUtil.validateToken(token);
    }
    
    public String getUsernameFromToken(String token) {
        return jwtUtil.extractUsername(token);
    }
}
