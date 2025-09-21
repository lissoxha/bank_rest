package com.example.bankcards.repository;

import com.example.bankcards.entity.Transaction;
import com.example.bankcards.entity.Transaction.TransactionStatus;
import com.example.bankcards.entity.Transaction.TransactionType;
import com.example.bankcards.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    
    List<Transaction> findByUser(User user);
    
    Page<Transaction> findByUser(User user, Pageable pageable);
    
    @Query("SELECT t FROM Transaction t WHERE t.user = :user AND " +
           "(:type IS NULL OR t.type = :type) AND " +
           "(:status IS NULL OR t.status = :status) AND " +
           "(:fromDate IS NULL OR t.createdAt >= :fromDate) AND " +
           "(:toDate IS NULL OR t.createdAt <= :toDate)")
    Page<Transaction> findByUserAndFilters(@Param("user") User user,
                                           @Param("type") TransactionType type,
                                           @Param("status") TransactionStatus status,
                                           @Param("fromDate") LocalDateTime fromDate,
                                           @Param("toDate") LocalDateTime toDate,
                                           Pageable pageable);
    
    @Query("SELECT t FROM Transaction t WHERE " +
           "(:type IS NULL OR t.type = :type) AND " +
           "(:status IS NULL OR t.status = :status) AND " +
           "(:fromDate IS NULL OR t.createdAt >= :fromDate) AND " +
           "(:toDate IS NULL OR t.createdAt <= :toDate) AND " +
           "(:username IS NULL OR LOWER(t.user.username) LIKE LOWER(CONCAT('%', :username, '%')))")
    Page<Transaction> findByFilters(@Param("type") TransactionType type,
                                    @Param("status") TransactionStatus status,
                                    @Param("fromDate") LocalDateTime fromDate,
                                    @Param("toDate") LocalDateTime toDate,
                                    @Param("username") String username,
                                    Pageable pageable);
    
    @Query("SELECT t FROM Transaction t WHERE t.status = 'PENDING' AND t.createdAt < :cutoffTime")
    List<Transaction> findPendingTransactionsOlderThan(@Param("cutoffTime") LocalDateTime cutoffTime);
    
    @Query("SELECT t FROM Transaction t WHERE t.fromCard = :card OR t.toCard = :card")
    List<Transaction> findByCard(@Param("card") com.example.bankcards.entity.Card card);
}
