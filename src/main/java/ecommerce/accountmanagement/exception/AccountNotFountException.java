package ecommerce.accountmanagement.exception;

import org.springframework.security.core.AuthenticationException;

public class AccountNotFountException extends AuthenticationException {

    public AccountNotFountException(final String message) {
        super(message);
    }
}