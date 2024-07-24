package ecommerce.accountmanagement.repository;

import ecommerce.accountmanagement.entity.Transaction;
import ecommerce.accountmanagement.enums.TransactionApprovalStatus;
import ecommerce.accountmanagement.enums.TransactionType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    List<Transaction> findAllByAccountId(final Long accountId);

    List<Transaction> findAllByTransactionApprovalStatus(final TransactionApprovalStatus transactionApprovalStatus);

    @Query("SELECT SUM(t.amount) FROM Transaction t WHERE t.accountId = :accountId AND t.transactionType = :transactionType AND t.transactionDate BETWEEN :startDate AND :endDate")
    Long findTotalAmountForGivenAccountByTransactionTypeAndDateRange(
            @Param("accountId") final Long accountId,
            @Param("transactionType") final TransactionType transactionType,
            @Param("startDate") final LocalDateTime startDate,
            @Param("endDate") final LocalDateTime endDate);

    @Query("SELECT SUM(t.amount) FROM Transaction t WHERE t.transactionType = :transactionType AND t.transactionDate BETWEEN :startDate AND :endDate")
    Long findTotalAmountByTransactionTypeAndDateRange(
            @Param("transactionType") final TransactionType transactionType,
            @Param("startDate") final LocalDateTime startDate,
            @Param("endDate") final LocalDateTime endDate);

}
