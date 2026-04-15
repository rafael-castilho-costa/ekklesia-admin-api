package br.com.ekklesia.ekklesia_admin_api.domain.vo.entity.member;

import br.com.ekklesia.ekklesia_admin_api.core.audit.AuditAction;
import br.com.ekklesia.ekklesia_admin_api.core.audit.AuditLogService;
import br.com.ekklesia.ekklesia_admin_api.domain.vo.entity.shared.persona.Persona;
import br.com.ekklesia.ekklesia_admin_api.domain.vo.entity.shared.persona.PersonaRepository;
import br.com.ekklesia.ekklesia_admin_api.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class MemberService {

    private final MemberRepository memberRepository;
    private final PersonaRepository personaRepository;
    private final AuditLogService auditLogService;

    public MemberResponse create(MemberRequest request) {
        Persona persona = findPersona(request.personaId());

        Member member = new Member();
        apply(member, request, persona);
        Member savedMember = memberRepository.save(member);

        auditLogService.register("Member", savedMember.getId(), AuditAction.CREATE, "Membro criado.");
        return MemberResponse.from(savedMember);
    }

    @Transactional(readOnly = true)
    public List<MemberResponse> list() {
        return memberRepository.findAll().stream().map(MemberResponse::from).toList();
    }

    @Transactional(readOnly = true)
    public MemberResponse getById(Integer id) {
        return MemberResponse.from(findMember(id));
    }

    public MemberResponse update(Integer id, MemberRequest request) {
        Member member = findMember(id);
        Persona persona = findPersona(request.personaId());
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
        return memberRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Membro nao encontrado."));
    }

    private Persona findPersona(Integer id) {
        return personaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Pessoa nao encontrada."));
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
