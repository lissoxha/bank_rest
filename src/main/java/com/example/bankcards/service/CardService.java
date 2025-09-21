package com.example.bankcards.service;

import com.example.bankcards.dto.CardDto;
import com.example.bankcards.dto.CreateCardRequest;
import com.example.bankcards.entity.Card;
import com.example.bankcards.entity.User;
import com.example.bankcards.exception.BusinessException;
import com.example.bankcards.exception.ResourceNotFoundException;
import com.example.bankcards.exception.UnauthorizedException;
import com.example.bankcards.repository.CardRepository;
import com.example.bankcards.util.EncryptionUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class CardService {
    
    @Autowired
    private CardRepository cardRepository;
    
    @Autowired
    private UserService userService;
    
    @Autowired
    private EncryptionUtil encryptionUtil;
    
    public CardDto createCard(CreateCardRequest request) {
        if (cardRepository.existsByCardNumber(request.getCardNumber())) {
            throw new BusinessException("Card number already exists");
        }
        
        User owner = userService.findUserEntityById(request.getOwnerId());
        
        Card card = new Card();
        card.setCardNumber(encryptionUtil.encryptCardNumber(request.getCardNumber()));
        card.setCardHolder(request.getCardHolder());
        card.setExpiryDate(request.getExpiryDate());
        card.setOwner(owner);
        
        Card savedCard = cardRepository.save(card);
        return convertToDto(savedCard);
    }
    
    @Transactional(readOnly = true)
    public CardDto getCardById(Long id) {
        Card card = cardRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Card not found with id: " + id));
        return convertToDto(card);
    }
    
    @Transactional(readOnly = true)
    public CardDto getCardByIdForUser(Long id, String username) {
        Card card = cardRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Card not found with id: " + id));
        
        if (!card.getOwner().getUsername().equals(username)) {
            throw new UnauthorizedException("You can only access your own cards");
        }
        
        return convertToDto(card);
    }
    
    @Transactional(readOnly = true)
    public Page<CardDto> getCardsByUser(String username, Pageable pageable) {
        User user = userService.findUserEntityByUsername(username);
        return cardRepository.findByOwner(user, pageable)
                .map(this::convertToDto);
    }
    
    @Transactional(readOnly = true)
    public Page<CardDto> searchCardsByUser(String username, Card.CardStatus status, String cardHolder, Pageable pageable) {
        User user = userService.findUserEntityByUsername(username);
        return cardRepository.findByOwnerAndFilters(user, status, cardHolder, pageable)
                .map(this::convertToDto);
    }
    
    @Transactional(readOnly = true)
    public Page<CardDto> getAllCards(Pageable pageable) {
        return cardRepository.findAll(pageable)
                .map(this::convertToDto);
    }
    
    @Transactional(readOnly = true)
    public Page<CardDto> searchAllCards(Card.CardStatus status, String cardHolder, String ownerUsername, Pageable pageable) {
        return cardRepository.findByFilters(status, cardHolder, ownerUsername, pageable)
                .map(this::convertToDto);
    }
    
    public CardDto updateCardStatus(Long id, Card.CardStatus status) {
        Card card = cardRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Card not found with id: " + id));
        
        card.setStatus(status);
        Card savedCard = cardRepository.save(card);
        return convertToDto(savedCard);
    }
    
    public CardDto blockCard(Long id, String username) {
        Card card = cardRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Card not found with id: " + id));
        
        if (!card.getOwner().getUsername().equals(username)) {
            throw new UnauthorizedException("You can only block your own cards");
        }
        
        if (card.getStatus() == Card.CardStatus.BLOCKED) {
            throw new BusinessException("Card is already blocked");
        }
        
        card.setStatus(Card.CardStatus.BLOCKED);
        Card savedCard = cardRepository.save(card);
        return convertToDto(savedCard);
    }
    
    public CardDto activateCard(Long id) {
        Card card = cardRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Card not found with id: " + id));
        
        if (card.isExpired()) {
            throw new BusinessException("Cannot activate expired card");
        }
        
        card.setStatus(Card.CardStatus.ACTIVE);
        Card savedCard = cardRepository.save(card);
        return convertToDto(savedCard);
    }
    
    public void deleteCard(Long id) {
        Card card = cardRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Card not found with id: " + id));
        
        cardRepository.delete(card);
    }
    
    public void updateExpiredCards() {
        List<Card> expiredCards = cardRepository.findExpiredCards(LocalDate.now());
        for (Card card : expiredCards) {
            card.setStatus(Card.CardStatus.EXPIRED);
            cardRepository.save(card);
        }
    }
    
    @Transactional(readOnly = true)
    public Card findCardEntityById(Long id) {
        return cardRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Card not found with id: " + id));
    }
    
    @Transactional(readOnly = true)
    public List<Card> getActiveCardsByUser(String username) {
        User user = userService.findUserEntityByUsername(username);
        return cardRepository.findActiveCardsByOwner(user, LocalDate.now());
    }
    
    private CardDto convertToDto(Card card) {
        String decryptedCardNumber = encryptionUtil.decryptCardNumber(card.getCardNumber());
        return new CardDto(
                card.getId(),
                decryptedCardNumber,
                card.getCardHolder(),
                card.getExpiryDate(),
                card.getStatus().name(),
                card.getBalance(),
                card.getOwner().getId(),
                card.getOwner().getUsername()
        );
    }
}
