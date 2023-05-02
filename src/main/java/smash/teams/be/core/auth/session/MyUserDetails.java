package smash.teams.be.core.auth.session;

import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import smash.teams.be.model.user.User;

import java.util.ArrayList;
import java.util.Collection;

@Setter
@Getter
public class MyUserDetails implements org.springframework.security.core.userdetails.UserDetails {
    private User user;

    public MyUserDetails(User user) {
        this.user = user;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        Collection<GrantedAuthority> collector = new ArrayList<>();
        collector.add(() -> user.getRole());
        return collector;
    }

    @Override
    public String getPassword() {
        return user.getPassword();
    }

    @Override
    public String getUsername() {
        return user.getEmail();
    } // username이 없어서 대신 email return

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
