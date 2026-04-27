package ltphat.cloudvault.backend.iam.infrastructure.security;

import lombok.RequiredArgsConstructor;
import ltphat.cloudvault.backend.iam.domain.repository.IUserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final IUserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        return userRepository.findByEmail(email)
                .map(user -> new org.springframework.security.core.userdetails.User(
                        user.getEmail(),
                        user.getPasswordHash(),
                        new ArrayList<>()
                ))
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + email));
    }
}
