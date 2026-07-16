package com.bank.core.infrastructure.persistence;

import com.bank.core.domain.enums.AccountStatus;
import com.bank.core.domain.enums.AccountType;
import com.bank.core.domain.model.Account;
import com.bank.core.domain.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AccountRepository extends JpaRepository<Account, String> {

    Optional<Account> findByAccountNumber(String accountNumber);

    boolean existsByAccountNumber(String accountNumber);

    List<Account> findByStatus(AccountStatus status);
    List<Account> findByAccountType(AccountType accountType);

    // NUEVO: Buscar cuentas por tipo y estado
    List<Account> findByAccountTypeAndStatus(AccountType accountType, AccountStatus status);

    // ===== Cuentas por dueño (para autorización) =====

    List<Account> findByUser(User user);

    List<Account> findByUserAndStatus(User user, AccountStatus status);

    List<Account> findByUserAndAccountType(User user, AccountType accountType);
}