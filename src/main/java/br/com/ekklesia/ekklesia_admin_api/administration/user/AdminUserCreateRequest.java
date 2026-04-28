package br.com.ekklesia.ekklesia_admin_api.administration.user;

import br.com.ekklesia.ekklesia_admin_api.domain.vo.enumeration.MaritalStatus;
import br.com.ekklesia.ekklesia_admin_api.domain.vo.enumeration.PersonaType;
import br.com.ekklesia.ekklesia_admin_api.user.UserScope;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;
import java.util.Set;

public record AdminUserCreateRequest(
        Long churchId,
        @NotNull UserScope scope,
        @NotNull PersonaType personaType,
        @Pattern(regexp = "\\d{11}|\\d{14}") String taxId,
        @NotBlank @Size(max = 200) String name,
        LocalDate birthDate,
        MaritalStatus maritalStatus,
        @Size(max = 20) String phone,
        @Email @Size(max = 150) String personaEmail,
        @Size(max = 255) String address,
        @NotBlank @Email @Size(max = 150) String email,
        @NotBlank String password,
        Boolean active,
        @NotEmpty Set<@NotBlank String> roles
) {
}
