package ecommerce.accountmanagement.service;

import ecommerce.accountmanagement.entity.Account;
import ecommerce.accountmanagement.exception.AccountNotFountException;
import ecommerce.accountmanagement.repository.AccountRepository;
import ecommerce.accountmanagement.util.UserDetailsUtil;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class AccountService {

    private static final String ACCOUNT_NOT_FOUNT = "Account not found!";

    private final AccountRepository accountRepository;
    private final UserService userService;

    @Transactional
    public void updateAccountBalance(final Long accountId, final Long amount) {
        final Account toBeUpdatedAccount = accountRepository
                                                .findById(accountId)
                                                .orElseThrow(() -> new AccountNotFountException(ACCOUNT_NOT_FOUNT));

        toBeUpdatedAccount.setBalance(toBeUpdatedAccount.getBalance() + amount);
        accountRepository.save(toBeUpdatedAccount);
    }

    public Optional<Account> findByUserId(final Long userId) {
        return accountRepository.findByUserId(userId);
    }

    public List<Account> getAllAccounts() {
        return accountRepository
                .findAll()
                .stream()
                .sorted(Comparator.comparing(Account::getBalance).reversed())
                .toList();
    }

    public void createAccount(final Long balance) {
        final Account account = new Account();
        account.setBalance(balance);
        account.setUserId(UserDetailsUtil.getCurrentUserId());
        account.setCreationDate(LocalDateTime.now());
        accountRepository.save(account);
    }

    public boolean userHasAccount() {
        return Optional.ofNullable(UserDetailsUtil.getCurrentUserId())
                .map(accountRepository::existsByUserId)
                .orElse(false);
    }

    public Long getAccountBalance(final Long userId) {
        return accountRepository
                .findByUserId(userId)
                .map(Account::getBalance)
                .orElseThrow(() -> new AccountNotFountException(ACCOUNT_NOT_FOUNT));
    }

    public String getAccountHolderName(final Long accountId) {
        return userService.getUserNameById(getUserIdFromAccountId(accountId));
    }

    private Long getUserIdFromAccountId(final Long accountId) {
        return accountRepository
                .findById(accountId)
                .orElseThrow(() -> new AccountNotFountException(ACCOUNT_NOT_FOUNT))
                .getUserId();
    }
}
