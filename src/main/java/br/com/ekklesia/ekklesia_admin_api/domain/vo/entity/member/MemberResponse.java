package br.com.ekklesia.ekklesia_admin_api.domain.vo.entity.member;

import br.com.ekklesia.ekklesia_admin_api.domain.vo.enumeration.Ministry;
import br.com.ekklesia.ekklesia_admin_api.domain.vo.enumeration.StatusMember;

import java.time.LocalDate;

public record MemberResponse(
        Integer id,
        Integer personaId,
        String personaName,
        Long churchId,
        String churchName,
        LocalDate membershipDate,
        LocalDate baptismDate,
        Boolean baptized,
        Ministry ministry,
        StatusMember statusMember,
        String notes
) {

    public static MemberResponse from(Member member) {
        return new MemberResponse(
                member.getId(),
                member.getPersona().getId(),
                member.getPersona().getName(),
                member.getPersona().getChurch() != null ? member.getPersona().getChurch().getId() : null,
                member.getPersona().getChurch() != null ? member.getPersona().getChurch().getName() : null,
                member.getMembershipDate(),
                member.getBaptismDate(),
                member.getBaptized(),
                member.getMinistry(),
                member.getStatusMember(),
                member.getNotes()
        );
    }
}
