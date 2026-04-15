package br.com.ekklesia.ekklesia_admin_api.church;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record ChurchRequest(
        @NotBlank @Size(max = 150) String name,
        @NotBlank @Pattern(regexp = "\\d{14}") String cnpj,
        @Size(max = 80) String city,
        @Pattern(regexp = "^[A-Z]{2}$") String state
) {
}
