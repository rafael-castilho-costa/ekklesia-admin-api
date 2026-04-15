package br.com.ekklesia.ekklesia_admin_api.domain.vo.entity.shared.persona;

import br.com.ekklesia.ekklesia_admin_api.domain.vo.enumeration.MaritalStatus;
import br.com.ekklesia.ekklesia_admin_api.domain.vo.enumeration.PersonaType;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public record PersonaRequest(
        @NotNull Long churchId,
        @NotNull PersonaType personaType,
        @Pattern(regexp = "\\d{11}|\\d{14}") String taxId,
        @NotBlank @Size(max = 200) String name,
        LocalDate birthDate,
        MaritalStatus maritalStatus,
        @Size(max = 20) String phone,
        @Email @Size(max = 150) String email,
        @Size(max = 255) String address
) {
}
