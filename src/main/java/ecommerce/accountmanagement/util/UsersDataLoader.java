package ecommerce.accountmanagement.util;

import ecommerce.accountmanagement.entity.User;
import ecommerce.accountmanagement.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.List;

import static ecommerce.accountmanagement.enums.UserRole.SERVICE;
import static ecommerce.accountmanagement.enums.UserRole.USER;

@Component
@AllArgsConstructor
public class UsersDataLoader implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        final User user1 = new User();
        user1.setName("User_1");
        user1.setEMail("user_1@agido.com");
        user1.setPassword(passwordEncoder.encode("u111"));
        user1.setUserRole(USER);

        final User user2 = new User();
        user2.setName("User_2");
        user2.setEMail("user_2@agido.com");
        user2.setPassword(passwordEncoder.encode("u222"));
        user2.setUserRole(USER);

        final User user3 = new User();
        user3.setName("User_3");
        user3.setEMail("user_3@agido.com");
        user3.setPassword(passwordEncoder.encode("u333"));
        user3.setUserRole(USER);

        final User service1 = new User();
        service1.setName("Service_1");
        service1.setEMail("service_1@agido.com");
        service1.setPassword(passwordEncoder.encode("s111"));
        service1.setUserRole(SERVICE);

        userRepository.saveAll(List.of(user1, user2, user3, service1));
    }
}
