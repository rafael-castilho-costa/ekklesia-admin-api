package br.com.ekklesia.ekklesia_admin_api.administration.user;

import br.com.ekklesia.ekklesia_admin_api.user.UserScope;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;

import java.util.Set;

public record AdminUserUpdateRequest(
        Long churchId,
        UserScope scope,
        @Size(max = 200) String name,
        @Email @Size(max = 150) String email,
        String password,
        Boolean active,
        Set<@NotBlank String> roles
) {
}
