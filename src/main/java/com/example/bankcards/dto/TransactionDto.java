package com.example.bankcards.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class TransactionDto {
    
    private Long id;
    
    @NotNull(message = "Amount is required")
    @DecimalMin(value = "0.01", message = "Amount must be greater than 0")
    private BigDecimal amount;
    
    private String type;
    
    private String status;
    
    private String description;
    
    private LocalDateTime createdAt;
    
    private LocalDateTime updatedAt;
    
    private Long fromCardId;
    
    private String fromCardNumber;
    
    private Long toCardId;
    
    private String toCardNumber;
    
    private Long userId;
    
    private String username;
    
    // Constructors
    public TransactionDto() {}
    
    public TransactionDto(Long id, BigDecimal amount, String type, String status, 
                         String description, LocalDateTime createdAt, LocalDateTime updatedAt,
                         Long fromCardId, String fromCardNumber, Long toCardId, 
                         String toCardNumber, Long userId, String username) {
        this.id = id;
        this.amount = amount;
        this.type = type;
        this.status = status;
        this.description = description;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.fromCardId = fromCardId;
        this.fromCardNumber = fromCardNumber;
        this.toCardId = toCardId;
        this.toCardNumber = toCardNumber;
        this.userId = userId;
        this.username = username;
    }
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public BigDecimal getAmount() {
        return amount;
    }
    
    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }
    
    public String getType() {
        return type;
    }
    
    public void setType(String type) {
        this.type = type;
    }
    
    public String getStatus() {
        return status;
    }
    
    public void setStatus(String status) {
        this.status = status;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
    
    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
    
    public Long getFromCardId() {
        return fromCardId;
    }
    
    public void setFromCardId(Long fromCardId) {
        this.fromCardId = fromCardId;
    }
    
    public String getFromCardNumber() {
        return fromCardNumber;
    }
    
    public void setFromCardNumber(String fromCardNumber) {
        this.fromCardNumber = fromCardNumber;
    }
    
    public Long getToCardId() {
        return toCardId;
    }
    
    public void setToCardId(Long toCardId) {
        this.toCardId = toCardId;
    }
    
    public String getToCardNumber() {
        return toCardNumber;
    }
    
    public void setToCardNumber(String toCardNumber) {
        this.toCardNumber = toCardNumber;
    }
    
    public Long getUserId() {
        return userId;
    }
    
    public void setUserId(Long userId) {
        this.userId = userId;
    }
    
    public String getUsername() {
        return username;
    }
    
    public void setUsername(String username) {
        this.username = username;
    }
}
