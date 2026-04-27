package br.com.ekklesia.ekklesia_admin_api.domain.vo.entity.member;

import br.com.ekklesia.ekklesia_admin_api.core.audit.AuditAction;
import br.com.ekklesia.ekklesia_admin_api.core.audit.AuditLogService;
import br.com.ekklesia.ekklesia_admin_api.domain.vo.entity.shared.persona.Persona;
import br.com.ekklesia.ekklesia_admin_api.domain.vo.entity.shared.persona.PersonaRepository;
import br.com.ekklesia.ekklesia_admin_api.domain.vo.enumeration.StatusMember;
import br.com.ekklesia.ekklesia_admin_api.exception.BusinessException;
import br.com.ekklesia.ekklesia_admin_api.exception.ResourceNotFoundException;
import br.com.ekklesia.ekklesia_admin_api.tenant.TenantContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Locale;

@Service
@RequiredArgsConstructor
@Transactional
public class MemberService {

    private final MemberRepository memberRepository;
    private final PersonaRepository personaRepository;
    private final AuditLogService auditLogService;

    public MemberResponse create(MemberRequest request) {
        Long churchId = getTenantChurchId();
        validateMemberPersonaAvailability(request.personaId(), churchId, null);
        Persona persona = findPersona(request.personaId(), churchId);

        Member member = new Member();
        apply(member, request, persona);
        Member savedMember = memberRepository.save(member);

        auditLogService.register("Member", savedMember.getId(), AuditAction.CREATE, "Membro criado.");
        return MemberResponse.from(savedMember);
    }

    @Transactional(readOnly = true)
    public List<MemberResponse> list(StatusMember statusMember, String search) {
        Long churchId = getTenantChurchId();
        String normalizedSearch = normalize(search);

        return memberRepository.findAllByPersonaChurchId(churchId).stream()
                .filter(member -> statusMember == null || statusMember == member.getStatusMember())
                .filter(member -> normalizedSearch == null || matchesSearch(member, normalizedSearch))
                .map(MemberResponse::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public MemberResponse getById(Integer id) {
        return MemberResponse.from(findMember(id));
    }

    public MemberResponse update(Integer id, MemberRequest request) {
        Long churchId = getTenantChurchId();
        Member member = findMember(id);
        validateMemberPersonaAvailability(request.personaId(), churchId, id);
        Persona persona = findPersona(request.personaId(), churchId);
        apply(member, request, persona);
        Member updatedMember = memberRepository.save(member);

        auditLogService.register("Member", updatedMember.getId(), AuditAction.UPDATE, "Membro atualizado.");
        return MemberResponse.from(updatedMember);
    }

    public void delete(Integer id) {
        Member member = findMember(id);
        memberRepository.delete(member);
        auditLogService.register("Member", id, AuditAction.DELETE, "Membro removido.");
    }

    private Member findMember(Integer id) {
        return memberRepository.findByIdAndPersonaChurchId(id, getTenantChurchId())
                .orElseThrow(() -> new ResourceNotFoundException("Membro nao encontrado."));
    }

    private Persona findPersona(Integer id, Long churchId) {
        return personaRepository.findByIdAndChurchId(id, churchId)
                .orElseThrow(() -> new ResourceNotFoundException("Pessoa nao encontrada."));
    }

    private Long getTenantChurchId() {
        Long churchId = TenantContext.getChurchId();
        if (churchId == null) {
            throw new BusinessException("Contexto da igreja nao informado.");
        }
        return churchId;
    }

    private void validateMemberPersonaAvailability(Integer personaId, Long churchId, Integer currentMemberId) {
        memberRepository.findByPersonaIdAndPersonaChurchId(personaId, churchId)
                .filter(existingMember -> !existingMember.getId().equals(currentMemberId))
                .ifPresent(existingMember -> {
                    throw new BusinessException("Pessoa informada ja possui cadastro de membro.");
                });
    }

    private boolean matchesSearch(Member member, String normalizedSearch) {
        String personaName = normalize(member.getPersona().getName());
        String ministryName = member.getMinistry() != null ? normalize(member.getMinistry().name()) : null;
        String ministryDescription = member.getMinistry() != null
                ? normalize(member.getMinistry().getDescription())
                : null;

        return contains(personaName, normalizedSearch)
                || contains(ministryName, normalizedSearch)
                || contains(ministryDescription, normalizedSearch);
    }

    private boolean contains(String value, String search) {
        return value != null && value.contains(search);
    }

    private String normalize(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        return value.trim().toLowerCase(Locale.ROOT);
    }

    private void apply(Member member, MemberRequest request, Persona persona) {
        member.setPersona(persona);
        member.setMembershipDate(request.membershipDate());
        member.setBaptismDate(request.baptismDate());
        member.setBaptized(request.baptized());
        member.setMinistry(request.ministry());
        member.setStatusMember(request.statusMember());
        member.setNotes(request.notes());
    }
}
