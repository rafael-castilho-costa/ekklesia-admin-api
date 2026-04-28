package br.com.ekklesia.ekklesia_admin_api.administration.persona;

import br.com.ekklesia.ekklesia_admin_api.church.Church;
import br.com.ekklesia.ekklesia_admin_api.church.ChurchRepository;
import br.com.ekklesia.ekklesia_admin_api.core.audit.AuditAction;
import br.com.ekklesia.ekklesia_admin_api.core.audit.AuditLogService;
import br.com.ekklesia.ekklesia_admin_api.domain.vo.entity.member.MemberRepository;
import br.com.ekklesia.ekklesia_admin_api.domain.vo.entity.shared.persona.Persona;
import br.com.ekklesia.ekklesia_admin_api.domain.vo.entity.shared.persona.PersonaRepository;
import br.com.ekklesia.ekklesia_admin_api.domain.vo.entity.shared.persona.PersonaRequest;
import br.com.ekklesia.ekklesia_admin_api.domain.vo.entity.shared.persona.PersonaResponse;
import br.com.ekklesia.ekklesia_admin_api.exception.BusinessException;
import br.com.ekklesia.ekklesia_admin_api.exception.ResourceNotFoundException;
import br.com.ekklesia.ekklesia_admin_api.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class AdminPersonaService {

    private final PersonaRepository personaRepository;
    private final ChurchRepository churchRepository;
    private final UserRepository userRepository;
    private final MemberRepository memberRepository;
    private final AuditLogService auditLogService;

    public List<PersonaResponse> list(Long churchId) {
        List<Persona> personas = churchId == null
                ? personaRepository.findAllByOrderByIdAsc()
                : personaRepository.findAllByChurchIdOrderByIdAsc(churchId);

        return personas.stream().map(PersonaResponse::from).toList();
    }

    public PersonaResponse getById(Integer id) {
        return PersonaResponse.from(findPersona(id));
    }

    public PersonaResponse create(PersonaRequest request) {
        Persona persona = new Persona();
        apply(persona, request);
        Persona savedPersona = personaRepository.save(persona);
        auditLogService.register("Persona", savedPersona.getId(), AuditAction.CREATE, "Pessoa criada pelo modulo Administracao.");
        return PersonaResponse.from(savedPersona);
    }

    public PersonaResponse update(Integer id, PersonaRequest request) {
        Persona persona = findPersona(id);
        apply(persona, request);
        Persona updatedPersona = personaRepository.save(persona);
        auditLogService.register("Persona", updatedPersona.getId(), AuditAction.UPDATE, "Pessoa atualizada pelo modulo Administracao.");
        return PersonaResponse.from(updatedPersona);
    }

    public void delete(Integer id) {
        Persona persona = findPersona(id);

        if (userRepository.existsByPersonaId(id) || memberRepository.existsByPersonaId(id)) {
            throw new BusinessException("A pessoa possui registros vinculados e nao pode ser removida.");
        }

        personaRepository.delete(persona);
        auditLogService.register("Persona", id, AuditAction.DELETE, "Pessoa removida pelo modulo Administracao.");
    }

    private Persona findPersona(Integer id) {
        return personaRepository.findDetailedById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Pessoa nao encontrada."));
    }

    private void apply(Persona persona, PersonaRequest request) {
        persona.setChurch(findChurch(request.churchId()));
        persona.setPersonaType(request.personaType());
        persona.setTaxId(request.taxId());
        persona.setName(request.name());
        persona.setBirthDate(request.birthDate());
        persona.setMaritalStatus(request.maritalStatus());
        persona.setPhone(request.phone());
        persona.setEmail(request.email());
        persona.setAddress(request.address());
    }

    private Church findChurch(Long churchId) {
        if (churchId == null) {
            return null;
        }

        return churchRepository.findById(churchId)
                .orElseThrow(() -> new ResourceNotFoundException("Igreja nao encontrada."));
    }
}
