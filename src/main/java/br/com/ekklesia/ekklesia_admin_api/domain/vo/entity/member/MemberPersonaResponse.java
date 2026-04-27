package br.com.ekklesia.ekklesia_admin_api.domain.vo.entity.member;

import br.com.ekklesia.ekklesia_admin_api.domain.vo.entity.shared.persona.Persona;
import br.com.ekklesia.ekklesia_admin_api.domain.vo.enumeration.MaritalStatus;

import java.time.LocalDate;

public record MemberPersonaResponse(
        Integer id,
        Long churchId,
        String name,
        String taxId,
        LocalDate birthDate,
        MaritalStatus maritalStatus,
        String phone,
        String email,
        String address
) {

    public static MemberPersonaResponse from(Persona persona) {
        return new MemberPersonaResponse(
                persona.getId(),
                persona.getChurch() != null ? persona.getChurch().getId() : null,
                persona.getName(),
                persona.getTaxId(),
                persona.getBirthDate(),
                persona.getMaritalStatus(),
                persona.getPhone(),
                persona.getEmail(),
                persona.getAddress()
        );
    }
}
