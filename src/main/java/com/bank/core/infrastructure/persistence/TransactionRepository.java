package com.bank.core.infrastructure.persistence;

import com.bank.core.domain.model.Transaction;
import com.bank.core.domain.model.Account;
import com.bank.core.domain.enums.TransactionType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, String> {

    /**
     * Busca transacciones de una cuenta (como origen o destino)
     */
    @Query("SELECT t FROM Transaction t WHERE t.sourceAccount = :account OR t.destinationAccount = :account")
    List<Transaction> findByAccount(@Param("account") Account account);

    /**
     * Busca transacciones de una cuenta en un rango de fechas
     */
    @Query("SELECT t FROM Transaction t WHERE (t.sourceAccount = :account OR t.destinationAccount = :account) " +
            "AND t.transactionDate BETWEEN :startDate AND :endDate")
    List<Transaction> findByAccountAndDateRange(@Param("account") Account account,
                                                @Param("startDate") LocalDateTime startDate,
                                                @Param("endDate") LocalDateTime endDate);

    /**
     * Busca transacciones por tipo
     */
    List<Transaction> findByTransactionType(TransactionType transactionType);

    /**
     * Busca transacciones por referencia
     */
    Optional<Transaction> findByReference(String reference);

    /**
     * Cuenta transacciones de una cuenta en un período
     */
    @Query("SELECT COUNT(t) FROM Transaction t WHERE (t.sourceAccount = :account OR t.destinationAccount = :account) " +
            "AND t.transactionDate >= :since")
    long countTransactionsSince(@Param("account") Account account,
                                @Param("since") LocalDateTime since);
}