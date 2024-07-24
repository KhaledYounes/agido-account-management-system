package ecommerce.accountmanagement.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

@Entity
@Table(name = "accounts")
@Data
public class Account {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long accountId;

    @Min(value = 0, message = "No negative balance allowed")
    @Column(nullable = false)
    private Long balance;

    @Column(nullable = false)
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private LocalDateTime creationDate;

    @Min(value = 1, message = "User ID must be a positive number")
    @JoinColumn(name = "user_id", nullable = false)
    private long userId;
}
