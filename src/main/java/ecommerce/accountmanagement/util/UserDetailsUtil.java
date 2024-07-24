package ecommerce.accountmanagement.util;

import ecommerce.accountmanagement.enums.UserRole;
import ecommerce.accountmanagement.exception.UserNotFountException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;

public class UserDetailsUtil {

    private static final String USER_NOT_FOUNT = "User not found!";

    public static CustomUserDetails getCurrentUserDetails() {
        final Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof CustomUserDetails) {
            return (CustomUserDetails) authentication.getPrincipal();
        }
        return null;
    }

    public static String getCurrentUsername() {
        return Optional
                .ofNullable(getCurrentUserDetails())
                .orElseThrow(() -> new UserNotFountException(USER_NOT_FOUNT))
                .getUsername();
    }

    public static Long getCurrentUserId() {
        return Optional
                .ofNullable(getCurrentUserDetails())
                .orElseThrow(() -> new UserNotFountException(USER_NOT_FOUNT))
                .getId();
    }

    public static boolean hasRole(final UserRole userRole) {
        return Optional
                .ofNullable(getCurrentUserDetails())
                .orElseThrow(() -> new UserNotFountException(USER_NOT_FOUNT))
                .getAuthorities()
                .contains(new SimpleGrantedAuthority(userRole.name()));
    }
}
