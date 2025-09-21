package com.example.bankcards.controller;

import com.example.bankcards.dto.CardDto;
import com.example.bankcards.dto.CreateCardRequest;
import com.example.bankcards.entity.Card;
import com.example.bankcards.service.CardService;
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
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/cards")
@Tag(name = "Cards", description = "Card management APIs")
public class CardController {
    
    @Autowired
    private CardService cardService;
    
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Create card", description = "Create a new card (Admin only)")
    public ResponseEntity<CardDto> createCard(@Valid @RequestBody CreateCardRequest request) {
        CardDto card = cardService.createCard(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(card);
    }
    
    @GetMapping
    @Operation(summary = "Get cards", description = "Get user's cards or all cards (Admin)")
    public ResponseEntity<Page<CardDto>> getCards(
            @Parameter(description = "Card status filter") @RequestParam(required = false) Card.CardStatus status,
            @Parameter(description = "Card holder name filter") @RequestParam(required = false) String cardHolder,
            @Parameter(description = "Owner username filter (Admin only)") @RequestParam(required = false) String ownerUsername,
            Pageable pageable,
            Authentication authentication) {
        
        String username = authentication.getName();
        boolean isAdmin = authentication.getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals("ROLE_ADMIN"));
        
        Page<CardDto> cards;
        if (isAdmin) {
            cards = cardService.searchAllCards(status, cardHolder, ownerUsername, pageable);
        } else {
            cards = cardService.searchCardsByUser(username, status, cardHolder, pageable);
        }
        
        return ResponseEntity.ok(cards);
    }
    
    @GetMapping("/{id}")
    @Operation(summary = "Get card by ID", description = "Get card details by ID")
    public ResponseEntity<CardDto> getCardById(
            @Parameter(description = "Card ID") @PathVariable Long id,
            Authentication authentication) {
        
        String username = authentication.getName();
        boolean isAdmin = authentication.getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals("ROLE_ADMIN"));
        
        CardDto card;
        if (isAdmin) {
            card = cardService.getCardById(id);
        } else {
            card = cardService.getCardByIdForUser(id, username);
        }
        
        return ResponseEntity.ok(card);
    }
    
    @PutMapping("/{id}/status")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Update card status", description = "Update card status (Admin only)")
    public ResponseEntity<CardDto> updateCardStatus(
            @Parameter(description = "Card ID") @PathVariable Long id,
            @Parameter(description = "New status") @RequestParam Card.CardStatus status) {
        CardDto card = cardService.updateCardStatus(id, status);
        return ResponseEntity.ok(card);
    }
    
    @PutMapping("/{id}/block")
    @Operation(summary = "Block card", description = "Block user's own card")
    public ResponseEntity<CardDto> blockCard(
            @Parameter(description = "Card ID") @PathVariable Long id,
            Authentication authentication) {
        String username = authentication.getName();
        CardDto card = cardService.blockCard(id, username);
        return ResponseEntity.ok(card);
    }
    
    @PutMapping("/{id}/activate")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Activate card", description = "Activate card (Admin only)")
    public ResponseEntity<CardDto> activateCard(
            @Parameter(description = "Card ID") @PathVariable Long id) {
        CardDto card = cardService.activateCard(id);
        return ResponseEntity.ok(card);
    }
    
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Delete card", description = "Delete card (Admin only)")
    public ResponseEntity<Void> deleteCard(
            @Parameter(description = "Card ID") @PathVariable Long id) {
        cardService.deleteCard(id);
        return ResponseEntity.noContent().build();
    }
}
