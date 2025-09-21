package com.example.bankcards.repository;

import com.example.bankcards.entity.Card;
import com.example.bankcards.entity.Card.CardStatus;
import com.example.bankcards.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface CardRepository extends JpaRepository<Card, Long> {
    
    Optional<Card> findByCardNumber(String cardNumber);
    
    List<Card> findByOwner(User owner);
    
    Page<Card> findByOwner(User owner, Pageable pageable);
    
    List<Card> findByOwnerAndStatus(User owner, CardStatus status);
    
    @Query("SELECT c FROM Card c WHERE c.owner = :owner AND " +
           "(:status IS NULL OR c.status = :status) AND " +
           "(:cardHolder IS NULL OR LOWER(c.cardHolder) LIKE LOWER(CONCAT('%', :cardHolder, '%')))")
    Page<Card> findByOwnerAndFilters(@Param("owner") User owner,
                                     @Param("status") CardStatus status,
                                     @Param("cardHolder") String cardHolder,
                                     Pageable pageable);
    
    @Query("SELECT c FROM Card c WHERE " +
           "(:status IS NULL OR c.status = :status) AND " +
           "(:cardHolder IS NULL OR LOWER(c.cardHolder) LIKE LOWER(CONCAT('%', :cardHolder, '%'))) AND " +
           "(:ownerUsername IS NULL OR LOWER(c.owner.username) LIKE LOWER(CONCAT('%', :ownerUsername, '%')))")
    Page<Card> findByFilters(@Param("status") CardStatus status,
                             @Param("cardHolder") String cardHolder,
                             @Param("ownerUsername") String ownerUsername,
                             Pageable pageable);
    
    @Query("SELECT c FROM Card c WHERE c.expiryDate < :date")
    List<Card> findExpiredCards(@Param("date") LocalDate date);
    
    @Query("SELECT c FROM Card c WHERE c.owner = :owner AND c.status = 'ACTIVE' AND c.expiryDate > :date")
    List<Card> findActiveCardsByOwner(@Param("owner") User owner, @Param("date") LocalDate date);
    
    boolean existsByCardNumber(String cardNumber);
}
