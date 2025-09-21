package com.example.bankcards.service;

import com.example.bankcards.dto.TransactionDto;
import com.example.bankcards.dto.TransferRequest;
import com.example.bankcards.entity.Card;
import com.example.bankcards.entity.Transaction;
import com.example.bankcards.entity.User;
import com.example.bankcards.exception.BusinessException;
import com.example.bankcards.exception.ResourceNotFoundException;
import com.example.bankcards.exception.UnauthorizedException;
import com.example.bankcards.repository.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
public class TransactionService {
    
    @Autowired
    private TransactionRepository transactionRepository;
    
    @Autowired
    private CardService cardService;
    
    @Autowired
    private UserService userService;
    
    public TransactionDto transferBetweenCards(TransferRequest request, String username) {
        User user = userService.findUserEntityByUsername(username);
        
        Card fromCard = cardService.findCardEntityById(request.getFromCardId());
        Card toCard = cardService.findCardEntityById(request.getToCardId());
        
        // Validate ownership
        if (!fromCard.getOwner().getUsername().equals(username)) {
            throw new UnauthorizedException("You can only transfer from your own cards");
        }
        
        if (!toCard.getOwner().getUsername().equals(username)) {
            throw new UnauthorizedException("You can only transfer to your own cards");
        }
        
        // Validate cards
        if (fromCard.getId().equals(toCard.getId())) {
            throw new BusinessException("Cannot transfer to the same card");
        }
        
        if (!fromCard.canTransfer(request.getAmount())) {
            throw new BusinessException("Insufficient funds or card is not available for transfer");
        }
        
        if (toCard.getStatus() != Card.CardStatus.ACTIVE || toCard.isExpired()) {
            throw new BusinessException("Destination card is not available for transfers");
        }
        
        // Create transaction
        Transaction transaction = new Transaction();
        transaction.setAmount(request.getAmount());
        transaction.setType(Transaction.TransactionType.TRANSFER);
        transaction.setStatus(Transaction.TransactionStatus.PENDING);
        transaction.setDescription(request.getDescription());
        transaction.setFromCard(fromCard);
        transaction.setToCard(toCard);
        transaction.setUser(user);
        
        Transaction savedTransaction = transactionRepository.save(transaction);
        
        // Process transfer
        try {
            fromCard.setBalance(fromCard.getBalance().subtract(request.getAmount()));
            toCard.setBalance(toCard.getBalance().add(request.getAmount()));
            
            savedTransaction.setStatus(Transaction.TransactionStatus.COMPLETED);
            transactionRepository.save(savedTransaction);
            
            return convertToDto(savedTransaction);
        } catch (Exception e) {
            savedTransaction.setStatus(Transaction.TransactionStatus.FAILED);
            transactionRepository.save(savedTransaction);
            throw new BusinessException("Transfer failed: " + e.getMessage());
        }
    }
    
    @Transactional(readOnly = true)
    public TransactionDto getTransactionById(Long id) {
        Transaction transaction = transactionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Transaction not found with id: " + id));
        return convertToDto(transaction);
    }
    
    @Transactional(readOnly = true)
    public TransactionDto getTransactionByIdForUser(Long id, String username) {
        Transaction transaction = transactionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Transaction not found with id: " + id));
        
        if (!transaction.getUser().getUsername().equals(username)) {
            throw new UnauthorizedException("You can only access your own transactions");
        }
        
        return convertToDto(transaction);
    }
    
    @Transactional(readOnly = true)
    public Page<TransactionDto> getTransactionsByUser(String username, Pageable pageable) {
        User user = userService.findUserEntityByUsername(username);
        return transactionRepository.findByUser(user, pageable)
                .map(this::convertToDto);
    }
    
    @Transactional(readOnly = true)
    public Page<TransactionDto> searchTransactionsByUser(String username, Transaction.TransactionType type, 
                                                        Transaction.TransactionStatus status,
                                                        LocalDateTime fromDate, LocalDateTime toDate, 
                                                        Pageable pageable) {
        User user = userService.findUserEntityByUsername(username);
        return transactionRepository.findByUserAndFilters(user, type, status, fromDate, toDate, pageable)
                .map(this::convertToDto);
    }
    
    @Transactional(readOnly = true)
    public Page<TransactionDto> getAllTransactions(Pageable pageable) {
        return transactionRepository.findAll(pageable)
                .map(this::convertToDto);
    }
    
    @Transactional(readOnly = true)
    public Page<TransactionDto> searchAllTransactions(Transaction.TransactionType type, 
                                                      Transaction.TransactionStatus status,
                                                      LocalDateTime fromDate, LocalDateTime toDate, 
                                                      String username, Pageable pageable) {
        return transactionRepository.findByFilters(type, status, fromDate, toDate, username, pageable)
                .map(this::convertToDto);
    }
    
    public TransactionDto cancelTransaction(Long id, String username) {
        Transaction transaction = transactionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Transaction not found with id: " + id));
        
        if (!transaction.getUser().getUsername().equals(username)) {
            throw new UnauthorizedException("You can only cancel your own transactions");
        }
        
        if (transaction.getStatus() != Transaction.TransactionStatus.PENDING) {
            throw new BusinessException("Only pending transactions can be cancelled");
        }
        
        transaction.setStatus(Transaction.TransactionStatus.CANCELLED);
        Transaction savedTransaction = transactionRepository.save(transaction);
        return convertToDto(savedTransaction);
    }
    
    public void processPendingTransactions() {
        LocalDateTime cutoffTime = LocalDateTime.now().minusHours(1);
        List<Transaction> pendingTransactions = transactionRepository.findPendingTransactionsOlderThan(cutoffTime);
        
        for (Transaction transaction : pendingTransactions) {
            transaction.setStatus(Transaction.TransactionStatus.FAILED);
            transactionRepository.save(transaction);
        }
    }
    
    private TransactionDto convertToDto(Transaction transaction) {
        String fromCardNumber = transaction.getFromCard() != null ? 
                transaction.getFromCard().getMaskedCardNumber() : null;
        String toCardNumber = transaction.getToCard() != null ? 
                transaction.getToCard().getMaskedCardNumber() : null;
        
        return new TransactionDto(
                transaction.getId(),
                transaction.getAmount(),
                transaction.getType().name(),
                transaction.getStatus().name(),
                transaction.getDescription(),
                transaction.getCreatedAt(),
                transaction.getUpdatedAt(),
                transaction.getFromCard() != null ? transaction.getFromCard().getId() : null,
                fromCardNumber,
                transaction.getToCard() != null ? transaction.getToCard().getId() : null,
                toCardNumber,
                transaction.getUser().getId(),
                transaction.getUser().getUsername()
        );
    }
}
