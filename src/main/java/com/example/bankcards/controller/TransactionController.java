package com.example.bankcards.controller;

import com.example.bankcards.dto.TransactionDto;
import com.example.bankcards.dto.TransferRequest;
import com.example.bankcards.entity.Transaction;
import com.example.bankcards.service.TransactionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/transactions")
@Tag(name = "Transactions", description = "Transaction management APIs")
public class TransactionController {
    
    @Autowired
    private TransactionService transactionService;
    
    @PostMapping("/transfer")
    @Operation(summary = "Transfer between cards", description = "Transfer money between user's own cards")
    public ResponseEntity<TransactionDto> transferBetweenCards(
            @Valid @RequestBody TransferRequest request,
            Authentication authentication) {
        String username = authentication.getName();
        TransactionDto transaction = transactionService.transferBetweenCards(request, username);
        return ResponseEntity.status(HttpStatus.CREATED).body(transaction);
    }
    
    @GetMapping
    @Operation(summary = "Get transactions", description = "Get user's transactions or all transactions (Admin)")
    public ResponseEntity<Page<TransactionDto>> getTransactions(
            @Parameter(description = "Transaction type filter") @RequestParam(required = false) Transaction.TransactionType type,
            @Parameter(description = "Transaction status filter") @RequestParam(required = false) Transaction.TransactionStatus status,
            @Parameter(description = "From date filter") @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fromDate,
            @Parameter(description = "To date filter") @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime toDate,
            @Parameter(description = "Username filter (Admin only)") @RequestParam(required = false) String username,
            Pageable pageable,
            Authentication authentication) {
        
        String currentUsername = authentication.getName();
        boolean isAdmin = authentication.getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals("ROLE_ADMIN"));
        
        Page<TransactionDto> transactions;
        if (isAdmin) {
            transactions = transactionService.searchAllTransactions(type, status, fromDate, toDate, username, pageable);
        } else {
            transactions = transactionService.searchTransactionsByUser(currentUsername, type, status, fromDate, toDate, pageable);
        }
        
        return ResponseEntity.ok(transactions);
    }
    
    @GetMapping("/{id}")
    @Operation(summary = "Get transaction by ID", description = "Get transaction details by ID")
    public ResponseEntity<TransactionDto> getTransactionById(
            @Parameter(description = "Transaction ID") @PathVariable Long id,
            Authentication authentication) {
        
        String username = authentication.getName();
        boolean isAdmin = authentication.getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals("ROLE_ADMIN"));
        
        TransactionDto transaction;
        if (isAdmin) {
            transaction = transactionService.getTransactionById(id);
        } else {
            transaction = transactionService.getTransactionByIdForUser(id, username);
        }
        
        return ResponseEntity.ok(transaction);
    }
    
    @PutMapping("/{id}/cancel")
    @Operation(summary = "Cancel transaction", description = "Cancel pending transaction")
    public ResponseEntity<TransactionDto> cancelTransaction(
            @Parameter(description = "Transaction ID") @PathVariable Long id,
            Authentication authentication) {
        String username = authentication.getName();
        TransactionDto transaction = transactionService.cancelTransaction(id, username);
        return ResponseEntity.ok(transaction);
    }
}
