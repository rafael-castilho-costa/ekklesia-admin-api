package br.com.ekklesia.ekklesia_admin_api.auth;

import br.com.ekklesia.ekklesia_admin_api.security.CustomUserDetails;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/auth")
public class AuthMeController {

    @GetMapping("/me")
    public AuthMeResponse me(Authentication authentication) {
        CustomUserDetails currentUser = (CustomUserDetails) authentication.getPrincipal();

        List<String> roles = authentication.getAuthorities()
                .stream()
                .map(GrantedAuthority::getAuthority)
                .sorted()
                .toList();

        return new AuthMeResponse(
                currentUser.getUser().getId(),
                currentUser.getUsername(),
                currentUser.getUser().getPersona().getName(),
                currentUser.getUser().getPersona().getId(),
                currentUser.getUser().getChurch() != null
                        ? currentUser.getUser().getChurch().getId()
                        : currentUser.getUser().getPersona().getChurch() != null
                                ? currentUser.getUser().getPersona().getChurch().getId()
                                : null,
                currentUser.getUser().getChurch() != null
                        ? currentUser.getUser().getChurch().getName()
                        : currentUser.getUser().getPersona().getChurch() != null
                                ? currentUser.getUser().getPersona().getChurch().getName()
                                : null,
                roles,
                currentUser.getScope().name(),
                currentUser.isPlatformAdmin()
        );
    }
}
