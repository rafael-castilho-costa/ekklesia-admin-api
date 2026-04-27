package br.com.ekklesia.ekklesia_admin_api.domain.vo.entity.shared.persona;

import br.com.ekklesia.ekklesia_admin_api.church.Church;
import br.com.ekklesia.ekklesia_admin_api.church.ChurchRepository;
import br.com.ekklesia.ekklesia_admin_api.core.audit.AuditAction;
import br.com.ekklesia.ekklesia_admin_api.core.audit.AuditLogService;
import br.com.ekklesia.ekklesia_admin_api.exception.BusinessException;
import br.com.ekklesia.ekklesia_admin_api.exception.ResourceNotFoundException;
import br.com.ekklesia.ekklesia_admin_api.tenant.TenantContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class PersonaService {

    private final PersonaRepository personaRepository;
    private final ChurchRepository churchRepository;
    private final AuditLogService auditLogService;

    public PersonaResponse create(PersonaRequest request) {
        Long churchId = getTenantChurchId();
        validateRequestChurch(request.churchId(), churchId);
        Church church = findChurch(churchId);

        Persona persona = new Persona();
        apply(persona, request, church);
        Persona savedPersona = personaRepository.save(persona);
        auditLogService.register("Persona", savedPersona.getId(), AuditAction.CREATE, "Pessoa criada.");
        return PersonaResponse.from(savedPersona);
    }

    @Transactional(readOnly = true)
    public List<PersonaResponse> list() {
        return personaRepository.findAllByChurchId(getTenantChurchId()).stream().map(PersonaResponse::from).toList();
    }

    @Transactional(readOnly = true)
    public PersonaResponse getById(Integer id) {
        return PersonaResponse.from(findPersona(id));
    }

    public PersonaResponse update(Integer id, PersonaRequest request) {
        Long churchId = getTenantChurchId();
        validateRequestChurch(request.churchId(), churchId);
        Church church = findChurch(churchId);
        Persona persona = findPersona(id);
        apply(persona, request, church);
        Persona updatedPersona = personaRepository.save(persona);
        auditLogService.register("Persona", updatedPersona.getId(), AuditAction.UPDATE, "Pessoa atualizada.");
        return PersonaResponse.from(updatedPersona);
    }

    public void delete(Integer id) {
        Persona persona = findPersona(id);
        personaRepository.delete(persona);
        auditLogService.register("Persona", id, AuditAction.DELETE, "Pessoa removida.");
    }

    private Persona findPersona(Integer id) {
        return personaRepository.findByIdAndChurchId(id, getTenantChurchId())
                .orElseThrow(() -> new ResourceNotFoundException("Pessoa nao encontrada."));
    }

    private Church findChurch(Long id) {
        return churchRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Igreja nao encontrada."));
    }

    private Long getTenantChurchId() {
        Long churchId = TenantContext.getChurchId();
        if (churchId == null) {
            throw new BusinessException("Contexto da igreja nao informado.");
        }
        return churchId;
    }

    private void validateRequestChurch(Long requestChurchId, Long tenantChurchId) {
        if (requestChurchId != null && !requestChurchId.equals(tenantChurchId)) {
            throw new BusinessException("churchId do corpo difere do header X-Church-Id.");
        }
    }

    private void apply(Persona persona, PersonaRequest request, Church church) {
        persona.setChurch(church);
        persona.setPersonaType(request.personaType());
        persona.setTaxId(request.taxId());
        persona.setName(request.name());
        persona.setBirthDate(request.birthDate());
        persona.setMaritalStatus(request.maritalStatus());
        persona.setPhone(request.phone());
        persona.setEmail(request.email());
        persona.setAddress(request.address());
    }
}
