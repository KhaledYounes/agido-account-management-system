package ecommerce.accountmanagement.security;

import ecommerce.accountmanagement.entity.User;
import ecommerce.accountmanagement.repository.UserRepository;
import ecommerce.accountmanagement.util.CustomUserDetails;
import lombok.AllArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(final String username) throws UsernameNotFoundException {
        User user = userRepository.findByName(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        return new CustomUserDetails(
                user.getUserId(),
                username,
                user.getPassword(),
                List.of(new SimpleGrantedAuthority(user.getUserRole().name()))
        );
    }
}
