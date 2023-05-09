package smash.teams.be.core.auth.session;

import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import smash.teams.be.model.user.User;
import smash.teams.be.model.user.UserRepository;

@RequiredArgsConstructor
@Service
public class MyUserDetailsService implements UserDetailsService {
    private final UserRepository userRepository;

    @Override
    public org.springframework.security.core.userdetails.UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User userPS = userRepository.findByEmail(username).orElseThrow(
                () -> new InternalAuthenticationServiceException("인증 실패"));
        return new MyUserDetails(userPS);
    }
}
