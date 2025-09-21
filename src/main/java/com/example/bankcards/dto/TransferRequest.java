package com.example.bankcards.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public class TransferRequest {
    
    @NotNull(message = "Amount is required")
    @DecimalMin(value = "0.01", message = "Amount must be greater than 0")
    private BigDecimal amount;
    
    @NotNull(message = "From card ID is required")
    private Long fromCardId;
    
    @NotNull(message = "To card ID is required")
    private Long toCardId;
    
    private String description;
    
    // Constructors
    public TransferRequest() {}
    
    public TransferRequest(BigDecimal amount, Long fromCardId, Long toCardId, String description) {
        this.amount = amount;
        this.fromCardId = fromCardId;
        this.toCardId = toCardId;
        this.description = description;
    }
    
    // Getters and Setters
    public BigDecimal getAmount() {
        return amount;
    }
    
    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }
    
    public Long getFromCardId() {
        return fromCardId;
    }
    
    public void setFromCardId(Long fromCardId) {
        this.fromCardId = fromCardId;
    }
    
    public Long getToCardId() {
        return toCardId;
    }
    
    public void setToCardId(Long toCardId) {
        this.toCardId = toCardId;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
}
