package br.com.ekklesia.ekklesia_admin_api.church;

import br.com.ekklesia.ekklesia_admin_api.core.audit.AuditAction;
import br.com.ekklesia.ekklesia_admin_api.core.audit.AuditLogService;
import br.com.ekklesia.ekklesia_admin_api.exception.BusinessException;
import br.com.ekklesia.ekklesia_admin_api.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class ChurchService {

    private final ChurchRepository churchRepository;
    private final AuditLogService auditLogService;

    public ChurchResponse create(ChurchRequest request) {
        validateUniqueCnpj(request.cnpj(), null);

        Church church = new Church();
        apply(church, request);
        Church savedChurch = churchRepository.save(church);
        auditLogService.register("Church", savedChurch.getId().intValue(), AuditAction.CREATE, "Igreja criada.");
        return ChurchResponse.from(savedChurch);
    }

    @Transactional(readOnly = true)
    public List<ChurchResponse> list() {
        return churchRepository.findAll().stream().map(ChurchResponse::from).toList();
    }

    @Transactional(readOnly = true)
    public ChurchResponse getById(Long id) {
        return ChurchResponse.from(findChurch(id));
    }

    public ChurchResponse update(Long id, ChurchRequest request) {
        validateUniqueCnpj(request.cnpj(), id);

        Church church = findChurch(id);
        apply(church, request);
        Church updatedChurch = churchRepository.save(church);
        auditLogService.register("Church", updatedChurch.getId().intValue(), AuditAction.UPDATE, "Igreja atualizada.");
        return ChurchResponse.from(updatedChurch);
    }

    public void delete(Long id) {
        Church church = findChurch(id);
        churchRepository.delete(church);
        auditLogService.register("Church", id.intValue(), AuditAction.DELETE, "Igreja removida.");
    }

    private Church findChurch(Long id) {
        return churchRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Igreja nao encontrada."));
    }

    private void validateUniqueCnpj(String cnpj, Long id) {
        boolean exists = id == null
                ? churchRepository.existsByCnpj(cnpj)
                : churchRepository.existsByCnpjAndIdNot(cnpj, id);

        if (exists) {
            throw new BusinessException("Ja existe uma igreja com o CNPJ informado.");
        }
    }

    private void apply(Church church, ChurchRequest request) {
        church.setName(request.name());
        church.setCnpj(request.cnpj());
        church.setCity(request.city());
        church.setState(request.state());
    }
}
