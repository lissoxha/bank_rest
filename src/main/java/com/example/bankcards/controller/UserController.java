package com.example.bankcards.controller;

import com.example.bankcards.dto.CreateUserRequest;
import com.example.bankcards.dto.UserDto;
import com.example.bankcards.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@PreAuthorize("hasRole('ADMIN')")
@Tag(name = "Users", description = "User management APIs (Admin only)")
public class UserController {
    
    @Autowired
    private UserService userService;
    
    @PostMapping
    @Operation(summary = "Create user", description = "Create a new user")
    public ResponseEntity<UserDto> createUser(@Valid @RequestBody CreateUserRequest request) {
        UserDto user = userService.createUser(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(user);
    }
    
    @GetMapping
    @Operation(summary = "Get all users", description = "Get all users with pagination")
    public ResponseEntity<Page<UserDto>> getAllUsers(Pageable pageable) {
        Page<UserDto> users = userService.getAllUsers(pageable);
        return ResponseEntity.ok(users);
    }
    
    @GetMapping("/search")
    @Operation(summary = "Search users", description = "Search users by filters")
    public ResponseEntity<Page<UserDto>> searchUsers(
            @Parameter(description = "Username filter") @RequestParam(required = false) String username,
            @Parameter(description = "Email filter") @RequestParam(required = false) String email,
            @Parameter(description = "First name filter") @RequestParam(required = false) String firstName,
            @Parameter(description = "Last name filter") @RequestParam(required = false) String lastName,
            Pageable pageable) {
        Page<UserDto> users = userService.searchUsers(username, email, firstName, lastName, pageable);
        return ResponseEntity.ok(users);
    }
    
    @GetMapping("/{id}")
    @Operation(summary = "Get user by ID", description = "Get user details by ID")
    public ResponseEntity<UserDto> getUserById(
            @Parameter(description = "User ID") @PathVariable Long id) {
        UserDto user = userService.getUserById(id);
        return ResponseEntity.ok(user);
    }
    
    @PutMapping("/{id}")
    @Operation(summary = "Update user", description = "Update user details")
    public ResponseEntity<UserDto> updateUser(
            @Parameter(description = "User ID") @PathVariable Long id,
            @Valid @RequestBody CreateUserRequest request) {
        UserDto user = userService.updateUser(id, request);
        return ResponseEntity.ok(user);
    }
    
    @PutMapping("/{id}/deactivate")
    @Operation(summary = "Deactivate user", description = "Deactivate user account")
    public ResponseEntity<Void> deactivateUser(
            @Parameter(description = "User ID") @PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }
    
    @PutMapping("/{id}/activate")
    @Operation(summary = "Activate user", description = "Activate user account")
    public ResponseEntity<Void> activateUser(
            @Parameter(description = "User ID") @PathVariable Long id) {
        userService.activateUser(id);
        return ResponseEntity.noContent().build();
    }
}
