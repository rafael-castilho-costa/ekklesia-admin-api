package br.com.ekklesia.ekklesia_admin_api.security;

import br.com.ekklesia.ekklesia_admin_api.user.User;
import br.com.ekklesia.ekklesia_admin_api.user.UserScope;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.stream.Collectors;

public class CustomUserDetails implements UserDetails {

    private final User user;

    public CustomUserDetails(User user) {
        this.user = user;
    }

    public Long getChurchId() {
        if (user.getChurch() != null) {
            return user.getChurch().getId();
        }

        if (user.getPersona() != null && user.getPersona().getChurch() != null) {
            return user.getPersona().getChurch().getId();
        }

        return null;
    }

    public UserScope getScope() {
        return user.getScope();
    }

    public boolean isPlatformAdmin() {
        return getAuthorities().stream()
                .anyMatch(authority -> "ROLE_ADMIN_MASTER".equals(authority.getAuthority()));
    }

    public User getUser() {
        return user;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return user.getRoles()
                .stream()
                .map(role -> new SimpleGrantedAuthority(role.getName()))
                .collect(Collectors.toUnmodifiableSet());
    }

    @Override
    public String getPassword() {
        return user.getPassword();
    }

    @Override
    public String getUsername() {
        return user.getEmail();
    }

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
        return user.isActive();
    }
}
