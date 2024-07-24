package ecommerce.accountmanagement.repository;

import ecommerce.accountmanagement.entity.Account;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AccountRepository extends JpaRepository<Account, Long> {

    boolean existsByUserId(final Long userId);
    Optional<Account> findByUserId(final Long userId);
}
