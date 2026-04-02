package br.com.ekklesia.ekklesia_admin_api.domain.vo.entity.member;

import br.com.ekklesia.ekklesia_admin_api.core.audit.AuditAction;
import br.com.ekklesia.ekklesia_admin_api.core.audit.AuditLogService;
import br.com.ekklesia.ekklesia_admin_api.domain.vo.entity.shared.persona.PersonaRepository;
import br.com.ekklesia.ekklesia_admin_api.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;
    private final PersonaRepository personaRepository;
    private final AuditLogService auditLogService;

    public Member update(Integer id, MemberRequest request) {
        Member member = memberRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Membro não encontrado"));

        String before = member.toString();
        applyUpdate(member, request);
        Member updatedMember = memberRepository.save(member);
        String after = updatedMember.toString();
        auditLogService.register("Member",
                updatedMember.getId(),
                AuditAction.UPDATE, "Antes: " + before + " | Depois: " + after
        );
        return updatedMember;
    }

    public void delete(Integer id) {
        Member member = memberRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Membro não encontrado."));
        String before = member.toString();
        memberRepository.delete(member);
        auditLogService.register("Member", id, AuditAction.DELETE, "Registro removido: " + before
        );
    }

    private void applyUpdate(Member member, MemberRequest request) {
        if (request.getPersonaId() != null) {
            member.setPersona(personaRepository.findById(request.getPersonaId())
                    .orElseThrow(() -> new ResourceNotFoundException("Pessoa não encontrada.")));
        }
    }
}
