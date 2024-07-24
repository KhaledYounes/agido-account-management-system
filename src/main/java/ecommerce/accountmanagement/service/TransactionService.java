package ecommerce.accountmanagement.service;

import ecommerce.accountmanagement.entity.Account;
import ecommerce.accountmanagement.entity.Transaction;
import ecommerce.accountmanagement.enums.TransactionApprovalStatus;
import ecommerce.accountmanagement.enums.TransactionType;
import ecommerce.accountmanagement.exception.AccountNotFountException;
import ecommerce.accountmanagement.exception.TransactionNotFountException;
import ecommerce.accountmanagement.repository.TransactionRepository;
import ecommerce.accountmanagement.util.UserDetailsUtil;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Comparator;
import java.util.List;

import static ecommerce.accountmanagement.enums.TransactionApprovalStatus.*;
import static ecommerce.accountmanagement.enums.TransactionType.DEPOSIT;
import static ecommerce.accountmanagement.enums.TransactionType.WITHDRAWAL;

@Service
@AllArgsConstructor
public class TransactionService {

    private static final String TRANSACTION_NOT_FOUNT = "Transaction not found!";
    private static final String ACCOUNT_NOT_FOUNT = "Account not found!";

    private final TransactionRepository transactionRepository;
    private final AccountService accountService;

    @Transactional
    public void updateTransactionApprovalStatus(final Long transactionId,
                                                final TransactionApprovalStatus transactionApprovalStatus) {

        final Transaction toBeUpdatedTransaction = transactionRepository.findById(transactionId)
                .orElseThrow(() -> new TransactionNotFountException(TRANSACTION_NOT_FOUNT));

        toBeUpdatedTransaction.setTransactionApprovalStatus(transactionApprovalStatus);
        transactionRepository.save(toBeUpdatedTransaction);
    }

    public List<Transaction> getTransactions(final Long userId) {
        return transactionRepository
                .findAllByAccountId(getAccountIdFromUserId(userId))
                .stream()
                .sorted(Comparator.comparing(Transaction::getTransactionDate).reversed())
                .toList();
    }

    public void depositAmount(final Long amount) {
        final Long accountId = getAccountIdFromUserId(UserDetailsUtil.getCurrentUserId());
        transactionRepository.save(createTransaction(accountId, amount, DEPOSIT, ACCEPTED));
        accountService.updateAccountBalance(accountId, amount);
    }

    public void withdrawAmount(final Long amount) {
        final Long accountId = getAccountIdFromUserId(UserDetailsUtil.getCurrentUserId());
        transactionRepository.save(createTransaction(accountId, amount, WITHDRAWAL, PENDING));
    }

    public Long getTotalAmountForGivenType(final TransactionType transactionType, final LocalDateTime startDate,
                                           final LocalDateTime endDate) {

        return transactionRepository.findTotalAmountByTransactionTypeAndDateRange(transactionType, startDate, endDate);
    }

    public Long getTotalDeposits(final LocalDate startDate, final LocalDate endDate) {
        return getTotalAmountForGivenType(DEPOSIT, startDate.atStartOfDay(), LocalDateTime.of(endDate, LocalTime.MAX));
    }

    public Long getTotalWithdrawals(final LocalDate startDate, final LocalDate endDate) {
        return getTotalAmountForGivenType(WITHDRAWAL, startDate.atStartOfDay(), LocalDateTime.of(endDate, LocalTime.MAX));
    }

    public Long getTotalAmountForGivenAccountAndType(final Long accountId,final TransactionType transactionType,
                                                     final LocalDateTime startDate, final LocalDateTime endDate) {

        return transactionRepository
                .findTotalAmountForGivenAccountByTransactionTypeAndDateRange(accountId, transactionType, startDate, endDate);
    }

    public Long getTotalDepositsForGivenUser(final Long userId, final LocalDate startDate, final LocalDate endDate) {
        return getTotalAmountForGivenAccountAndType(getAccountIdFromUserId(userId), DEPOSIT,
                startDate.atStartOfDay(), LocalDateTime.of(endDate, LocalTime.MAX));
    }

    public Long getTotalWithdrawalsForGivenUser(final Long userId, final LocalDate startDate, final LocalDate endDate) {
        return getTotalAmountForGivenAccountAndType(getAccountIdFromUserId(userId), WITHDRAWAL,
                startDate.atStartOfDay(), LocalDateTime.of(endDate, LocalTime.MAX));
    }

    private Transaction createTransaction(final Long accountId, final Long amount, final TransactionType transactionType,
                                          final TransactionApprovalStatus transactionApprovalStatus) {

        final Transaction transaction = new Transaction();
        transaction.setAccountId(accountId);
        transaction.setAmount(amount);
        transaction.setTransactionType(transactionType);
        transaction.setTransactionApprovalStatus(transactionApprovalStatus);
        transaction.setTransactionDate(LocalDateTime.now());

        return transaction;
    }

    public List<Transaction> getPendingTransactions() {
        return transactionRepository.findAllByTransactionApprovalStatus(PENDING)
                .stream()
                .sorted(Comparator.comparing(Transaction::getTransactionDate))
                .toList();
    }

    public void acceptTransaction(final Long transactionId) {
        updateTransactionApprovalStatus(transactionId, ACCEPTED);

        final Transaction transaction = transactionRepository
                .findById(transactionId)
                .orElseThrow(() -> new TransactionNotFountException(TRANSACTION_NOT_FOUNT));

        accountService.updateAccountBalance(transaction.getAccountId(), -transaction.getAmount());
    }

    public void denyTransaction(final Long transactionId) {
        updateTransactionApprovalStatus(transactionId, DENIED);
    }

    private Long getAccountIdFromUserId(final Long userId) {
        return accountService
                .findByUserId(userId)
                .map(Account::getAccountId)
                .orElseThrow(() -> new AccountNotFountException(ACCOUNT_NOT_FOUNT));
    }
}
