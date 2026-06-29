package com.bank.core.infrastructure.persistence;

import com.bank.core.domain.model.Account;
import com.bank.core.domain.enums.AccountStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

/**
 * Repositorio para la entidad Account.
 *
 * JpaRepository<Account, String>:
 * - Account: Tipo de entidad
 * - String: Tipo de la clave primaria (ID)
 *
 * Spring Data JPA proporciona automáticamente:
 * - save(), findById(), findAll(), delete(), etc.
 * - Métodos de consulta derivados del nombre
 */
@Repository
public interface AccountRepository extends JpaRepository<Account, String> {

    /**
     * Busca una cuenta por su número de cuenta.
     * Spring Data JPA genera automáticamente la consulta.
     *
     * @param accountNumber Número de cuenta
     * @return Optional con la cuenta si existe
     */
    Optional<Account> findByAccountNumber(String accountNumber);

    /**
     * Verifica si existe una cuenta con ese número.
     *
     * @param accountNumber Número de cuenta
     * @return true si existe
     */
    boolean existsByAccountNumber(String accountNumber);

    /**
     * Busca cuentas por estado.
     *
     * @param status Estado de la cuenta
     * @return Lista de cuentas con ese estado
     */
    List<Account> findByStatus(AccountStatus status);

    /**
     * Busca cuentas con saldo menor a un valor.
     *
     * @param amount Saldo límite
     * @return Lista de cuentas con saldo bajo
     */
    List<Account> findByBalanceLessThan(BigDecimal amount);

    /**
     * Consulta personalizada con JPQL.
     *
     * JPQL es similar a SQL pero trabaja con objetos Java.
     *
     * @param accountNumber Número de cuenta
     * @return Cuenta con bloqueo pesimista (para transacciones)
     */
    @Query("SELECT a FROM Account a WHERE a.accountNumber = :accountNumber")
    Optional<Account> findWithLocking(@Param("accountNumber") String accountNumber);

    /**
     * Actualiza el saldo de una cuenta.
     *
     * @param accountId ID de la cuenta
     * @param newBalance Nuevo saldo
     * @return Número de filas actualizadas
     */
    @Query("UPDATE Account a SET a.balance = :newBalance WHERE a.id = :accountId")
    int updateBalance(@Param("accountId") String accountId,
                      @Param("newBalance") BigDecimal newBalance);
}