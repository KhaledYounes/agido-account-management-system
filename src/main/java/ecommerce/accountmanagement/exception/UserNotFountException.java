package ecommerce.accountmanagement.exception;

import org.springframework.security.core.AuthenticationException;

public class UserNotFountException extends AuthenticationException {

    public UserNotFountException(final String message) {
        super(message);
    }
}
