package com.bank.core.service.impl;

import com.bank.core.api.dto.response.AccountResponse;
import com.bank.core.api.mapper.AccountMapper;
import com.bank.core.api.mapper.TransactionMapper;
import com.bank.core.domain.enums.AccountType;
import com.bank.core.domain.enums.Role;
import com.bank.core.domain.exception.AccountNotFoundException;
import com.bank.core.domain.exception.InsufficientBalanceException;
import com.bank.core.domain.model.Account;
import com.bank.core.domain.model.User;
import com.bank.core.infrastructure.persistence.AccountRepository;
import com.bank.core.infrastructure.persistence.TransactionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.AccessDeniedException;

import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

/**
 * Tests unitarios de AccountServiceImpl.
 * No levantan contexto de Spring (rápidos): las dependencias (repositorios,
 * mappers) se simulan con Mockito.
 */
@ExtendWith(MockitoExtension.class)
class AccountServiceImplTest {

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private AccountMapper accountMapper;

    @Mock
    private TransactionMapper transactionMapper;

    @InjectMocks
    private AccountServiceImpl accountService;

    private User owner;
    private User otherUser;
    private User admin;

    @BeforeEach
    void setUp() {
        owner = new User("juan", "juan@bank.com", "hash", Role.USER);
        owner.setId("user-1");

        otherUser = new User("pedro", "pedro@bank.com", "hash", Role.USER);
        otherUser.setId("user-2");

        admin = new User("admin", "admin@bank.com", "hash", Role.ADMIN);
        admin.setId("user-admin");

        // El mapper simplemente devuelve una respuesta vacía; no es lo que
        // estamos probando en estos tests (nos importa la lógica de negocio,
        // no el mapeo). lenient() porque no todos los tests llegan a usarlo
        // (los que verifican excepciones cortan antes).
        org.mockito.Mockito.lenient().when(accountMapper.toResponse(any(Account.class)))
                .thenReturn(new AccountResponse());
    }

    private Account accountOf(User user, String accountNumber, BigDecimal balance) {
        Account account = new Account(accountNumber, AccountType.CHECKING, "USD");
        account.setId("acc-" + accountNumber);
        account.setUser(user);
        // El balance por defecto ya es BigDecimal.ZERO; deposit() no permite
        // depositar montos <= 0, así que solo lo llamamos si hay algo que sumar.
        if (balance.compareTo(BigDecimal.ZERO) > 0) {
            account.deposit(balance);
        }
        return account;
    }

    // ===== createAccount =====

    @Test
    void createAccount_deberiaAsignarElUsuarioActualComoDueno() {
        when(accountRepository.existsByAccountNumber("ACC-001")).thenReturn(false);
        when(accountRepository.save(any(Account.class))).thenAnswer(inv -> inv.getArgument(0));

        accountService.createAccount(owner, "ACC-001", AccountType.SAVINGS, "USD");

        // Verificamos que se haya guardado una cuenta con el dueño correcto
        org.mockito.ArgumentCaptor<Account> captor = org.mockito.ArgumentCaptor.forClass(Account.class);
        org.mockito.Mockito.verify(accountRepository).save(captor.capture());
        assertThat(captor.getValue().getUser()).isEqualTo(owner);
        assertThat(captor.getValue().belongsTo(owner)).isTrue();
    }

    @Test
    void createAccount_conNumeroDeCuentaDuplicado_lanzaExcepcion() {
        when(accountRepository.existsByAccountNumber("ACC-001")).thenReturn(true);

        assertThatThrownBy(() ->
                accountService.createAccount(owner, "ACC-001", AccountType.SAVINGS, "USD"))
                .isInstanceOf(IllegalArgumentException.class);
    }

    // ===== deposit =====

    @Test
    void deposit_montoPositivo_actualizaElSaldo() {
        Account account = accountOf(owner, "ACC-001", BigDecimal.ZERO);
        when(accountRepository.findByAccountNumber("ACC-001")).thenReturn(Optional.of(account));
        when(accountRepository.save(any(Account.class))).thenAnswer(inv -> inv.getArgument(0));

        accountService.deposit(owner, "ACC-001", new BigDecimal("100.00"));

        assertThat(account.getBalance()).isEqualByComparingTo("100.00");
    }

    @Test
    void deposit_montoNegativoOCero_lanzaExcepcion() {
        assertThatThrownBy(() -> accountService.deposit(owner, "ACC-001", BigDecimal.ZERO))
                .isInstanceOf(IllegalArgumentException.class);

        assertThatThrownBy(() -> accountService.deposit(owner, "ACC-001", new BigDecimal("-10")))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void deposit_cuentaQueNoPertenceAlUsuario_lanzaAccessDenied() {
        Account account = accountOf(otherUser, "ACC-001", BigDecimal.ZERO);
        when(accountRepository.findByAccountNumber("ACC-001")).thenReturn(Optional.of(account));

        assertThatThrownBy(() -> accountService.deposit(owner, "ACC-001", new BigDecimal("50")))
                .isInstanceOf(AccessDeniedException.class);
    }

    @Test
    void deposit_comoAdmin_puedeOperarCuentaDeOtroUsuario() {
        Account account = accountOf(otherUser, "ACC-001", BigDecimal.ZERO);
        when(accountRepository.findByAccountNumber("ACC-001")).thenReturn(Optional.of(account));
        when(accountRepository.save(any(Account.class))).thenAnswer(inv -> inv.getArgument(0));

        accountService.deposit(admin, "ACC-001", new BigDecimal("50"));

        assertThat(account.getBalance()).isEqualByComparingTo("50");
    }

    @Test
    void deposit_cuentaInexistente_lanzaAccountNotFound() {
        when(accountRepository.findByAccountNumber("ACC-999")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> accountService.deposit(owner, "ACC-999", new BigDecimal("10")))
                .isInstanceOf(AccountNotFoundException.class);
    }

    // ===== withdraw =====

    @Test
    void withdraw_conSaldoSuficiente_actualizaElSaldo() {
        Account account = accountOf(owner, "ACC-001", new BigDecimal("100.00"));
        when(accountRepository.findByAccountNumber("ACC-001")).thenReturn(Optional.of(account));
        when(accountRepository.save(any(Account.class))).thenAnswer(inv -> inv.getArgument(0));

        accountService.withdraw(owner, "ACC-001", new BigDecimal("40.00"));

        assertThat(account.getBalance()).isEqualByComparingTo("60.00");
    }

    @Test
    void withdraw_sinSaldoSuficiente_lanzaInsufficientBalance() {
        Account account = accountOf(owner, "ACC-001", new BigDecimal("10.00"));
        when(accountRepository.findByAccountNumber("ACC-001")).thenReturn(Optional.of(account));

        assertThatThrownBy(() -> accountService.withdraw(owner, "ACC-001", new BigDecimal("50.00")))
                .isInstanceOf(InsufficientBalanceException.class);

        // El saldo NO debe haber cambiado
        assertThat(account.getBalance()).isEqualByComparingTo("10.00");
    }

    @Test
    void withdraw_cuentaQueNoPertenceAlUsuario_lanzaAccessDenied() {
        Account account = accountOf(otherUser, "ACC-001", new BigDecimal("100.00"));
        when(accountRepository.findByAccountNumber("ACC-001")).thenReturn(Optional.of(account));

        assertThatThrownBy(() -> accountService.withdraw(owner, "ACC-001", new BigDecimal("10.00")))
                .isInstanceOf(AccessDeniedException.class);
    }

    // ===== transfer =====

    @Test
    void transfer_conSaldoSuficiente_muevePlataEntreCuentas() {
        Account source = accountOf(owner, "ACC-001", new BigDecimal("100.00"));
        Account destination = accountOf(otherUser, "ACC-002", new BigDecimal("0.00"));

        when(accountRepository.findByAccountNumber("ACC-001")).thenReturn(Optional.of(source));
        when(accountRepository.findByAccountNumber("ACC-002")).thenReturn(Optional.of(destination));
        when(accountRepository.save(any(Account.class))).thenAnswer(inv -> inv.getArgument(0));

        accountService.transfer(owner, "ACC-001", "ACC-002", new BigDecimal("30.00"));

        assertThat(source.getBalance()).isEqualByComparingTo("70.00");
        assertThat(destination.getBalance()).isEqualByComparingTo("30.00");
    }

    @Test
    void transfer_puedeEnviarleDineroACuentaDeOtroUsuario() {
        // El dueño de la cuenta ORIGEN sí importa, pero NO el dueño del destino:
        // cualquiera puede recibir una transferencia (como en la vida real).
        Account source = accountOf(owner, "ACC-001", new BigDecimal("100.00"));
        Account destination = accountOf(otherUser, "ACC-002", new BigDecimal("0.00"));

        when(accountRepository.findByAccountNumber("ACC-001")).thenReturn(Optional.of(source));
        when(accountRepository.findByAccountNumber("ACC-002")).thenReturn(Optional.of(destination));
        when(accountRepository.save(any(Account.class))).thenAnswer(inv -> inv.getArgument(0));

        accountService.transfer(owner, "ACC-001", "ACC-002", new BigDecimal("30.00"));

        assertThat(destination.getBalance()).isEqualByComparingTo("30.00");
    }

    @Test
    void transfer_sinSerDuenoDeLaCuentaOrigen_lanzaAccessDenied() {
        Account source = accountOf(otherUser, "ACC-001", new BigDecimal("100.00"));
        when(accountRepository.findByAccountNumber("ACC-001")).thenReturn(Optional.of(source));

        assertThatThrownBy(() ->
                accountService.transfer(owner, "ACC-001", "ACC-002", new BigDecimal("10.00")))
                .isInstanceOf(AccessDeniedException.class);
    }

    @Test
    void transfer_ALaMismaCuenta_lanzaExcepcion() {
        assertThatThrownBy(() ->
                accountService.transfer(owner, "ACC-001", "ACC-001", new BigDecimal("10.00")))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void transfer_sinSaldoSuficiente_lanzaInsufficientBalance() {
        Account source = accountOf(owner, "ACC-001", new BigDecimal("5.00"));
        Account destination = accountOf(otherUser, "ACC-002", new BigDecimal("0.00"));

        when(accountRepository.findByAccountNumber("ACC-001")).thenReturn(Optional.of(source));
        when(accountRepository.findByAccountNumber("ACC-002")).thenReturn(Optional.of(destination));

        assertThatThrownBy(() ->
                accountService.transfer(owner, "ACC-001", "ACC-002", new BigDecimal("50.00")))
                .isInstanceOf(InsufficientBalanceException.class);
    }

    // ===== blockAccount / activateAccount (solo ADMIN) =====

    @Test
    void blockAccount_comoUsuarioNormal_lanzaAccessDenied() {
        assertThatThrownBy(() -> accountService.blockAccount(owner, "ACC-001"))
                .isInstanceOf(AccessDeniedException.class);
    }

    @Test
    void blockAccount_comoAdmin_funcionaSobreCualquierCuenta() {
        Account account = accountOf(otherUser, "ACC-001", BigDecimal.ZERO);
        when(accountRepository.findByAccountNumber("ACC-001")).thenReturn(Optional.of(account));
        when(accountRepository.save(any(Account.class))).thenAnswer(inv -> inv.getArgument(0));

        accountService.blockAccount(admin, "ACC-001");

        assertThat(account.getStatus().name()).isEqualTo("BLOCKED");
    }
}
