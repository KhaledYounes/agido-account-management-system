package ecommerce.accountmanagement.service;

import ecommerce.accountmanagement.entity.Account;
import ecommerce.accountmanagement.entity.Transaction;
import ecommerce.accountmanagement.exception.TransactionNotFountException;
import ecommerce.accountmanagement.repository.TransactionRepository;
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

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

import static ecommerce.accountmanagement.enums.TransactionApprovalStatus.*;
import static ecommerce.accountmanagement.enums.TransactionType.DEPOSIT;
import static ecommerce.accountmanagement.enums.TransactionType.WITHDRAWAL;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@SpringBootTest
class TransactionServiceTest {

    private static final Long FIRST_TRANSACTION_ID = 1L;
    private static final Long SECOND_TRANSACTION_ID = 2L;
    private static final Long ACCOUNT_ID = 1L;
    private static final Long USER_ID = 1L;
    private static final Long FIRST_AMOUNT = 1000L;
    private static final Long SECOND_AMOUNT = 500L;

    @MockBean private TransactionRepository transactionRepository;
    @MockBean private AccountService accountService;

    @Autowired private TransactionService transactionService;

    @Captor private ArgumentCaptor<Transaction> transactionArgumentCaptor;

    private Transaction firstTransaction;
    private Transaction secondTransaction;
    private Account account;

    @BeforeEach
    void setUp() {
        firstTransaction = new Transaction();
        firstTransaction.setTransactionId(FIRST_TRANSACTION_ID);
        firstTransaction.setAmount(FIRST_AMOUNT);
        firstTransaction.setTransactionType(DEPOSIT);
        firstTransaction.setTransactionApprovalStatus(PENDING);
        firstTransaction.setAccountId(ACCOUNT_ID);
        firstTransaction.setTransactionDate(LocalDateTime.now().minusDays(1));

        secondTransaction = new Transaction();
        secondTransaction.setTransactionId(SECOND_TRANSACTION_ID);
        secondTransaction.setAmount(SECOND_AMOUNT);
        secondTransaction.setTransactionType(WITHDRAWAL);
        secondTransaction.setTransactionApprovalStatus(PENDING);
        secondTransaction.setAccountId(ACCOUNT_ID);
        secondTransaction.setTransactionDate(LocalDateTime.now());

        account = new Account();
        account.setAccountId(ACCOUNT_ID);
        account.setBalance(FIRST_AMOUNT);
        account.setUserId(USER_ID);
        account.setCreationDate(LocalDateTime.now().minusDays(1));
    }

    @Test
    void updateTransactionApprovalStatus() {
        when(transactionRepository.findById(FIRST_TRANSACTION_ID)).thenReturn(Optional.of(firstTransaction));

        transactionService.updateTransactionApprovalStatus(FIRST_TRANSACTION_ID, ACCEPTED);

        verify(transactionRepository).findById(FIRST_TRANSACTION_ID);
        verify(transactionRepository).save(transactionArgumentCaptor.capture());

        final Transaction updatedTransaction = transactionArgumentCaptor.getValue();
        assertEquals(ACCEPTED, updatedTransaction.getTransactionApprovalStatus());
    }

    @Test
    void getTransactions() {
        when(accountService.findByUserId(USER_ID)).thenReturn(Optional.of(account));
        when(transactionRepository.findAllByAccountId(ACCOUNT_ID)).thenReturn(List.of(firstTransaction, secondTransaction));

        final List<Transaction> transactions = transactionService.getTransactions(USER_ID);

        assertEquals(2, transactions.size());
        assertEquals(secondTransaction, transactions.get(0));
        assertEquals(firstTransaction, transactions.get(1));
        verify(transactionRepository).findAllByAccountId(ACCOUNT_ID);
    }

    @Test
    void depositAmount() {
        try (final MockedStatic<UserDetailsUtil> utilities = Mockito.mockStatic(UserDetailsUtil.class)) {
            utilities.when(UserDetailsUtil::getCurrentUserId).thenReturn(USER_ID);
            when(accountService.findByUserId(USER_ID)).thenReturn(Optional.of(account));

            transactionService.depositAmount(FIRST_AMOUNT);

            verify(transactionRepository).save(transactionArgumentCaptor.capture());
            verify(accountService).updateAccountBalance(ACCOUNT_ID, FIRST_AMOUNT);

            final Transaction savedTransaction = transactionArgumentCaptor.getValue();
            assertEquals(FIRST_AMOUNT, savedTransaction.getAmount());
            assertEquals(DEPOSIT, savedTransaction.getTransactionType());
            assertEquals(ACCEPTED, savedTransaction.getTransactionApprovalStatus());
            assertEquals(ACCOUNT_ID, savedTransaction.getAccountId());
        }
    }

    @Test
    void withdrawAmount() {
        try (MockedStatic<UserDetailsUtil> utilities = Mockito.mockStatic(UserDetailsUtil.class)) {
            utilities.when(UserDetailsUtil::getCurrentUserId).thenReturn(USER_ID);
            when(accountService.findByUserId(USER_ID)).thenReturn(Optional.of(account));

            transactionService.withdrawAmount(SECOND_AMOUNT);

            verify(transactionRepository).save(transactionArgumentCaptor.capture());

            final Transaction savedTransaction = transactionArgumentCaptor.getValue();
            assertEquals(SECOND_AMOUNT, savedTransaction.getAmount());
            assertEquals(WITHDRAWAL, savedTransaction.getTransactionType());
            assertEquals(PENDING, savedTransaction.getTransactionApprovalStatus());
            assertEquals(ACCOUNT_ID, savedTransaction.getAccountId());
        }
    }

    @Test
    void getTotalAmountForGivenType() {
        final LocalDateTime startDate = LocalDateTime.now().minusDays(1);
        final LocalDateTime endDate = LocalDateTime.now();
        when(transactionRepository.findTotalAmountByTransactionTypeAndDateRange(DEPOSIT, startDate, endDate))
                .thenReturn(FIRST_AMOUNT + SECOND_AMOUNT);

        final Long totalAmount = transactionService.getTotalAmountForGivenType(DEPOSIT, startDate, endDate);

        assertEquals(FIRST_AMOUNT + SECOND_AMOUNT, totalAmount);
        verify(transactionRepository)
                .findTotalAmountByTransactionTypeAndDateRange(DEPOSIT, startDate, endDate);
    }

    @Test
    void getTotalDeposits() {
        final LocalDate startDate = LocalDate.now().minusDays(1);
        final LocalDate endDate = LocalDate.now();
        when(transactionRepository
                .findTotalAmountByTransactionTypeAndDateRange(DEPOSIT, startDate.atStartOfDay(),
                        LocalDateTime.of(endDate, LocalTime.MAX))).thenReturn(FIRST_AMOUNT);

        final Long totalDeposits = transactionService.getTotalDeposits(startDate, endDate);

        assertEquals(FIRST_AMOUNT, totalDeposits);
        verify(transactionRepository)
                .findTotalAmountByTransactionTypeAndDateRange(DEPOSIT, startDate.atStartOfDay(),
                        LocalDateTime.of(endDate, LocalTime.MAX));
    }

    @Test
    void getTotalWithdrawals() {
        final LocalDate startDate = LocalDate.now().minusDays(1);
        final LocalDate endDate = LocalDate.now();
        when(transactionRepository
                .findTotalAmountByTransactionTypeAndDateRange(WITHDRAWAL, startDate.atStartOfDay(),
                        LocalDateTime.of(endDate, LocalTime.MAX))).thenReturn(SECOND_AMOUNT);

        final Long totalWithdrawals = transactionService.getTotalWithdrawals(startDate, endDate);

        assertEquals(SECOND_AMOUNT, totalWithdrawals);
        verify(transactionRepository)
                .findTotalAmountByTransactionTypeAndDateRange(WITHDRAWAL, startDate.atStartOfDay(),
                        LocalDateTime.of(endDate, LocalTime.MAX));
    }

    @Test
    void getTotalAmountForGivenAccountAndType() {
        final LocalDateTime startDate = LocalDateTime.now().minusDays(1);
        final LocalDateTime endDate = LocalDateTime.now();
        when(transactionRepository
                .findTotalAmountForGivenAccountByTransactionTypeAndDateRange(ACCOUNT_ID, DEPOSIT, startDate, endDate))
                .thenReturn(FIRST_AMOUNT);

        final Long totalAmount = transactionService
                .getTotalAmountForGivenAccountAndType(ACCOUNT_ID, DEPOSIT, startDate, endDate);

        assertEquals(FIRST_AMOUNT, totalAmount);
        verify(transactionRepository)
                .findTotalAmountForGivenAccountByTransactionTypeAndDateRange(ACCOUNT_ID, DEPOSIT, startDate, endDate);
    }

    @Test
    void getTotalDepositsForGivenUser() {
        final LocalDate startDate = LocalDate.now().minusDays(1);
        final LocalDate endDate = LocalDate.now();
        when(accountService.findByUserId(USER_ID)).thenReturn(Optional.of(account));
        when(transactionRepository
                .findTotalAmountForGivenAccountByTransactionTypeAndDateRange(ACCOUNT_ID, DEPOSIT, startDate.atStartOfDay(),
                        LocalDateTime.of(endDate, LocalTime.MAX))).thenReturn(FIRST_AMOUNT);

        final Long totalDeposits = transactionService.getTotalDepositsForGivenUser(USER_ID, startDate, endDate);

        assertEquals(FIRST_AMOUNT, totalDeposits);
        verify(transactionRepository)
                .findTotalAmountForGivenAccountByTransactionTypeAndDateRange(ACCOUNT_ID, DEPOSIT, startDate.atStartOfDay(),
                        LocalDateTime.of(endDate, LocalTime.MAX));
    }

    @Test
    void getTotalWithdrawalsForGivenUser() {
        final LocalDate startDate = LocalDate.now().minusDays(1);
        final LocalDate endDate = LocalDate.now();
        when(accountService.findByUserId(USER_ID)).thenReturn(Optional.of(account));
        when(transactionRepository
                .findTotalAmountForGivenAccountByTransactionTypeAndDateRange(ACCOUNT_ID, WITHDRAWAL, startDate.atStartOfDay(),
                        LocalDateTime.of(endDate, LocalTime.MAX))).thenReturn(SECOND_AMOUNT);

        final Long totalWithdrawals = transactionService.getTotalWithdrawalsForGivenUser(USER_ID, startDate, endDate);

        assertEquals(SECOND_AMOUNT, totalWithdrawals);
        verify(transactionRepository)
                .findTotalAmountForGivenAccountByTransactionTypeAndDateRange(ACCOUNT_ID, WITHDRAWAL, startDate.atStartOfDay(),
                        LocalDateTime.of(endDate, LocalTime.MAX));
    }

    @Test
    void getPendingTransactions() {
        when(transactionRepository.findAllByTransactionApprovalStatus(PENDING))
                .thenReturn(List.of(firstTransaction, secondTransaction));

        final List<Transaction> pendingTransactions = transactionService.getPendingTransactions();

        assertEquals(2, pendingTransactions.size());
        assertEquals(firstTransaction, pendingTransactions.get(0));
        assertEquals(secondTransaction, pendingTransactions.get(1));
        verify(transactionRepository).findAllByTransactionApprovalStatus(PENDING);
    }

    @Test
    void acceptTransaction() {
        when(transactionRepository.findById(FIRST_TRANSACTION_ID)).thenReturn(Optional.of(firstTransaction));

        transactionService.acceptTransaction(FIRST_TRANSACTION_ID);

        verify(transactionRepository, times(2)).findById(FIRST_TRANSACTION_ID);
        verify(transactionRepository).save(transactionArgumentCaptor.capture());
        verify(accountService).updateAccountBalance(ACCOUNT_ID, -FIRST_AMOUNT);

        final Transaction updatedTransaction = transactionArgumentCaptor.getValue();
        assertEquals(ACCEPTED, updatedTransaction.getTransactionApprovalStatus());
    }

    @Test
    void denyTransaction() {
        when(transactionRepository.findById(FIRST_TRANSACTION_ID)).thenReturn(Optional.of(firstTransaction));

        transactionService.denyTransaction(FIRST_TRANSACTION_ID);

        verify(transactionRepository).findById(FIRST_TRANSACTION_ID);
        verify(transactionRepository).save(transactionArgumentCaptor.capture());

        final Transaction updatedTransaction = transactionArgumentCaptor.getValue();
        assertEquals(DENIED, updatedTransaction.getTransactionApprovalStatus());
    }

    @Test
    void updateTransactionApprovalStatusShouldThrowTransactionNotFound() {
        when(transactionRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(TransactionNotFountException.class,
                () -> transactionService.updateTransactionApprovalStatus(FIRST_TRANSACTION_ID, ACCEPTED));
    }

    @Test
    void acceptTransactionShouldThrowTransactionNotFound() {
        when(transactionRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(TransactionNotFountException.class, () -> transactionService.acceptTransaction(anyLong()));
    }

    @Test
    void denyTransactionShouldThrowTransactionNotFound() {
        when(transactionRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(TransactionNotFountException.class, () -> transactionService.denyTransaction(anyLong()));
    }
}
