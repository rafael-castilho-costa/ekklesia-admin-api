package br.com.ekklesia.ekklesia_admin_api.domain.vo.entity.shared.persona;

import br.com.ekklesia.ekklesia_admin_api.domain.vo.enumeration.MaritalStatus;
import br.com.ekklesia.ekklesia_admin_api.domain.vo.enumeration.PersonaType;

import java.time.LocalDate;

public record PersonaResponse(
        Integer id,
        Long churchId,
        String churchName,
        PersonaType personaType,
        String taxId,
        String name,
        LocalDate birthDate,
        MaritalStatus maritalStatus,
        String phone,
        String email,
        String address
) {

    public static PersonaResponse from(Persona persona) {
        return new PersonaResponse(
                persona.getId(),
                persona.getChurch() != null ? persona.getChurch().getId() : null,
                persona.getChurch() != null ? persona.getChurch().getName() : null,
                persona.getPersonaType(),
                persona.getTaxId(),
                persona.getName(),
                persona.getBirthDate(),
                persona.getMaritalStatus(),
                persona.getPhone(),
                persona.getEmail(),
                persona.getAddress()
        );
    }
}
