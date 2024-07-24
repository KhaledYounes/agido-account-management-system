package ecommerce.accountmanagement.service;

import ecommerce.accountmanagement.entity.User;
import ecommerce.accountmanagement.exception.UserNotFountException;
import ecommerce.accountmanagement.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class UserService {

    private static final String USER_NOT_FOUNT = "User not found!";

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public void registerUser (final User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        userRepository.save(user);
    }

    public String getUserNameById(final Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFountException(USER_NOT_FOUNT))
                .getName();
    }
}
