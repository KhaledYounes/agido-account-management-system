package ecommerce.accountmanagement.service;

import ecommerce.accountmanagement.entity.User;
import ecommerce.accountmanagement.exception.UserNotFountException;
import ecommerce.accountmanagement.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static ecommerce.accountmanagement.enums.UserRole.USER;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@SpringBootTest
class UserServiceTest {

    private static final Long USER_ID = 1L;
    private static final String USER_NAME = "Khaled";
    private static final String E_MAIL = "khaled@agido.com";
    private static final String PASSWORD = "password";
    private static final String USER_NOT_FOUND = "User not found!";

    @MockBean private UserRepository userRepository;

    @MockBean private PasswordEncoder passwordEncoder;

    @Autowired private UserService userService;

    @Captor private ArgumentCaptor<User> userArgumentCaptor;

    private User user;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setUserId(USER_ID);
        user.setName(USER_NAME);
        user.setEMail(E_MAIL);
        user.setPassword(PASSWORD);
        user.setUserRole(USER);
    }

    @Test
    void registerUser() {
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");

        userService.registerUser(user);

        verify(passwordEncoder).encode(PASSWORD);
        verify(userRepository).save(userArgumentCaptor.capture());

        final User capturedUser = userArgumentCaptor.getValue();
        assertEquals("encodedPassword", capturedUser.getPassword());
        assertEquals(user.getName(), capturedUser.getName());
        assertEquals(user.getEMail(), capturedUser.getEMail());
        assertEquals(user.getUserRole(), capturedUser.getUserRole());
    }

    @Test
    void getUserNameById() {
        when(userRepository.findById(USER_ID)).thenReturn(Optional.of(user));

        final String userName = userService.getUserNameById(USER_ID);

        assertEquals(USER_NAME, userName);
        verify(userRepository).findById(USER_ID);
    }

    @Test
    void getUserNameByIdShouldThrowUserNotFound() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());

        final UserNotFountException exception = assertThrows(UserNotFountException.class,
                () -> userService.getUserNameById(anyLong()));

        assertEquals(USER_NOT_FOUND, exception.getMessage());
        verify(userRepository).findById(anyLong());
    }
}
