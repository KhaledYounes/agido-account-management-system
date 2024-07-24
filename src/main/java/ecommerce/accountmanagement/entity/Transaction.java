package ecommerce.accountmanagement.entity;

import ecommerce.accountmanagement.enums.TransactionApprovalStatus;
import ecommerce.accountmanagement.enums.TransactionType;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

@Entity
@Table(name = "transactions")
@Data
public class Transaction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long transactionId;

    @Min(value = 1, message = "Amount must be a positive number")
    @Column(nullable = false)
    private Long amount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TransactionType transactionType;

    @Enumerated(EnumType.ORDINAL)
    @Column(nullable = false)
    private TransactionApprovalStatus transactionApprovalStatus;

    @Min(value = 1, message = "Account ID must be a positive number")
    @JoinColumn(name = "account_id", nullable = false)
    private Long accountId;

    @Column(nullable = false)
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private LocalDateTime transactionDate;
}