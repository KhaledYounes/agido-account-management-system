package ecommerce.accountmanagement.exception;

import org.springframework.security.core.AuthenticationException;

public class TransactionNotFountException extends AuthenticationException {

    public TransactionNotFountException(final String message) {
        super(message);
    }
}