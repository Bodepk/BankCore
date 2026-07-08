package com.bank.core.infrastructure.persistence;

import com.bank.core.domain.enums.TransactionType;
import com.bank.core.domain.model.Account;
import com.bank.core.domain.model.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, String> {

    @Query("SELECT t FROM Transaction t WHERE t.sourceAccount = :account OR t.destinationAccount = :account")
    List<Transaction> findByAccount(@Param("account") Account account);

    @Query("SELECT t FROM Transaction t WHERE (t.sourceAccount = :account OR t.destinationAccount = :account) " +
            "AND t.transactionDate BETWEEN :startDate AND :endDate")
    List<Transaction> findByAccountAndDateRange(@Param("account") Account account,
                                                @Param("startDate") LocalDateTime startDate,
                                                @Param("endDate") LocalDateTime endDate);

    List<Transaction> findByTransactionType(TransactionType transactionType);

    Optional<Transaction> findByReference(String reference);

    //Buscar transacciones por tipo y rango de fechas
    List<Transaction> findByTransactionTypeAndTransactionDateBetween(
            TransactionType transactionType,
            LocalDateTime startDate,
            LocalDateTime endDate);

    //NUEVO: Buscar transacciones recientes de una cuenta (limitado)
    @Query("SELECT t FROM Transaction t WHERE t.sourceAccount = :account OR t.destinationAccount = :account " +
            "ORDER BY t.transactionDate DESC LIMIT :limit")
    List<Transaction> findRecentTransactions(@Param("account") Account account,
                                             @Param("limit") int limit);

    //Contar transacciones por tipo
    @Query("SELECT COUNT(t) FROM Transaction t WHERE t.transactionType = :type")
    long countByTransactionType(@Param("type") TransactionType type);

    //Suma total de transacciones por tipo
    @Query("SELECT COALESCE(SUM(t.amount), 0) FROM Transaction t WHERE t.transactionType = :type")
    double sumByTransactionType(@Param("type") TransactionType type);
}