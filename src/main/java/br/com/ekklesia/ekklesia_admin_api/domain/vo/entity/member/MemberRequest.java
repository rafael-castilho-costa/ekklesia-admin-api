package br.com.ekklesia.ekklesia_admin_api.domain.vo.entity.member;

import br.com.ekklesia.ekklesia_admin_api.domain.vo.enumeration.Ministry;
import br.com.ekklesia.ekklesia_admin_api.domain.vo.enumeration.StatusMember;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public record MemberRequest(
        @NotNull Integer personaId,
        LocalDate membershipDate,
        LocalDate baptismDate,
        Boolean baptized,
        Ministry ministry,
        @NotNull StatusMember statusMember,
        @Size(max = 4000) String notes
) {
}
