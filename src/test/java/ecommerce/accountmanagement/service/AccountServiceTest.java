package ecommerce.accountmanagement.service;

import ecommerce.accountmanagement.entity.Account;
import ecommerce.accountmanagement.exception.AccountNotFountException;
import ecommerce.accountmanagement.repository.AccountRepository;
import ecommerce.accountmanagement.util.UserDetailsUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
class AccountServiceTest {

    private static final Long FIRST_ACCOUNT_ID = 1L;
    private static final Long SECOND_ACCOUNT_ID = 2L;
    private static final Long FIRST_USER_ID = 1L;
    private static final Long SECOND_USER_ID = 2L;
    private static final Long FIRST_INITIAL_BALANCE = 1000L;
    private static final Long SECOND_INITIAL_BALANCE = 2000L;
    private static final String FIRST_USER_NAME = "Khaled";

    @MockBean private AccountRepository accountRepository;
    @MockBean private UserService userService;

    @Autowired private AccountService accountService;

    @Captor private ArgumentCaptor<Account> accountArgumentCaptor;

    private Account firstAccount;
    private Account secondAccount;

    @BeforeEach
    void setUp() {
        firstAccount = new Account();
        firstAccount.setAccountId(FIRST_ACCOUNT_ID);
        firstAccount.setBalance(FIRST_INITIAL_BALANCE);
        firstAccount.setUserId(FIRST_USER_ID);
        firstAccount.setCreationDate(LocalDateTime.now());

        secondAccount = new Account();
        secondAccount.setAccountId(SECOND_ACCOUNT_ID);
        secondAccount.setBalance(SECOND_INITIAL_BALANCE);
        secondAccount.setUserId(SECOND_USER_ID);
        secondAccount.setCreationDate(LocalDateTime.now());
    }

    @Test
    void updateAccountBalance() {
        when(accountRepository.findById(FIRST_ACCOUNT_ID)).thenReturn(Optional.of(firstAccount));

        accountService.updateAccountBalance(FIRST_ACCOUNT_ID, 500L);

        verify(accountRepository).findById(FIRST_ACCOUNT_ID);
        verify(accountRepository).save(accountArgumentCaptor.capture());

        final Account updatedAccount = accountArgumentCaptor.getValue();
        assertEquals(1500L, updatedAccount.getBalance());
    }

    @Test
    void updateAccountBalanceShouldThrowAccountNotFound() {
        when(accountRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(AccountNotFountException.class,
                () -> accountService.updateAccountBalance(anyLong(), FIRST_INITIAL_BALANCE));
    }

    @Test
    void findByUserId() {
        when(accountRepository.findByUserId(FIRST_USER_ID)).thenReturn(Optional.of(firstAccount));

        final Optional<Account> result = accountService.findByUserId(FIRST_USER_ID);

        assertTrue(result.isPresent());
        assertEquals(firstAccount, result.get());
        verify(accountRepository).findByUserId(FIRST_USER_ID);
    }

    @Test
    void getAllAccounts() {
        final List<Account> accounts = List.of(firstAccount, secondAccount);
        when(accountRepository.findAll()).thenReturn(accounts);

        final List<Account> result = accountService.getAllAccounts();

        assertEquals(2, result.size());
        assertEquals(secondAccount, result.get(0));
        assertEquals(firstAccount, result.get(1));

        verify(accountRepository).findAll();
    }

    @Test
    void createAccount() {
        try (final MockedStatic<UserDetailsUtil> utilities = Mockito.mockStatic(UserDetailsUtil.class)) {
            utilities.when(UserDetailsUtil::getCurrentUserId).thenReturn(FIRST_ACCOUNT_ID);

            accountService.createAccount(FIRST_INITIAL_BALANCE);

            verify(accountRepository).save(accountArgumentCaptor.capture());

            final Account createdAccount = accountArgumentCaptor.getValue();
            assertEquals(FIRST_INITIAL_BALANCE, createdAccount.getBalance());
            assertEquals(FIRST_ACCOUNT_ID, createdAccount.getUserId());
            assertNotNull(createdAccount.getCreationDate());
        }
    }

    @Test
    void userHasAccount() {
        try (final MockedStatic<UserDetailsUtil> utilities = Mockito.mockStatic(UserDetailsUtil.class)) {
            utilities.when(UserDetailsUtil::getCurrentUserId).thenReturn(FIRST_ACCOUNT_ID);
            when(accountRepository.existsByUserId(FIRST_USER_ID)).thenReturn(true);

            final boolean result = accountService.userHasAccount();

            assertTrue(result);
            verify(accountRepository).existsByUserId(FIRST_USER_ID);
        }
    }

    @Test
    void getAccountBalance() {
        when(accountRepository.findByUserId(FIRST_USER_ID)).thenReturn(Optional.of(firstAccount));

        final Long balance = accountService.getAccountBalance(FIRST_USER_ID);

        assertEquals(FIRST_INITIAL_BALANCE, balance);
        verify(accountRepository).findByUserId(FIRST_USER_ID);
    }

    @Test
    void getAccountBalanceShouldThrowAccountNotFound() {
        when(accountRepository.findByUserId(anyLong())).thenReturn(Optional.empty());

        assertThrows(AccountNotFountException.class, () -> accountService.getAccountBalance(anyLong()));
    }

    @Test
    void getAccountHolderName() {
        when(accountRepository.findById(FIRST_ACCOUNT_ID)).thenReturn(Optional.of(firstAccount));
        when(userService.getUserNameById(FIRST_USER_ID)).thenReturn(FIRST_USER_NAME);

        final String accountHolderName = accountService.getAccountHolderName(FIRST_ACCOUNT_ID);

        assertEquals(FIRST_USER_NAME, accountHolderName);
        verify(accountRepository).findById(FIRST_ACCOUNT_ID);
        verify(userService).getUserNameById(FIRST_USER_ID);
    }

    @Test
    void getAccountHolderNameShouldThrowAccountNotFound() {
        when(accountRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(AccountNotFountException.class, () -> accountService.getAccountHolderName(anyLong()));
    }
}
