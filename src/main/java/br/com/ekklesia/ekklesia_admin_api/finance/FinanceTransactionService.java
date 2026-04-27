package br.com.ekklesia.ekklesia_admin_api.finance;

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

import java.time.LocalDate;
import java.util.List;
import java.util.Locale;

@Service
@RequiredArgsConstructor
@Transactional
public class FinanceTransactionService {

    private final FinanceTransactionRepository financeTransactionRepository;
    private final ChurchRepository churchRepository;
    private final AuditLogService auditLogService;

    @Transactional(readOnly = true)
    public List<FinanceTransactionResponse> list(
            FinanceTransactionType type,
            String category,
            String paymentMethod,
            LocalDate startDate,
            LocalDate endDate
    ) {
        validateDateRange(startDate, endDate);
        Long churchId = getTenantChurchId();
        String normalizedCategory = normalize(category);
        String normalizedPaymentMethod = normalize(paymentMethod);

        return financeTransactionRepository.findAllByChurchIdOrderByTransactionDateDescIdDesc(churchId).stream()
                .filter(transaction -> type == null || transaction.getType() == type)
                .filter(transaction -> normalizedCategory == null || normalize(transaction.getCategory()).equals(normalizedCategory))
                .filter(transaction -> normalizedPaymentMethod == null || normalize(transaction.getPaymentMethod()).equals(normalizedPaymentMethod))
                .filter(transaction -> startDate == null || !transaction.getTransactionDate().isBefore(startDate))
                .filter(transaction -> endDate == null || !transaction.getTransactionDate().isAfter(endDate))
                .map(FinanceTransactionResponse::from)
                .toList();
    }

    public FinanceTransactionResponse create(FinanceTransactionRequest request) {
        Church church = findChurchFromTenant();

        FinanceTransaction financeTransaction = new FinanceTransaction();
        financeTransaction.setChurch(church);
        apply(financeTransaction, request);

        FinanceTransaction savedTransaction = financeTransactionRepository.save(financeTransaction);
        auditLogService.register("FinanceTransaction", savedTransaction.getId(), AuditAction.CREATE, "Lancamento financeiro criado.");
        return FinanceTransactionResponse.from(savedTransaction);
    }

    private Church findChurchFromTenant() {
        Long churchId = getTenantChurchId();
        return churchRepository.findById(churchId)
                .orElseThrow(() -> new ResourceNotFoundException("Igreja não encontrada."));
    }

    private Long getTenantChurchId() {
        Long churchId = TenantContext.getChurchId();
        if (churchId == null) {
            throw new BusinessException("Contexto da igreja não informado.");
        }
        return churchId;
    }

    private void validateDateRange(LocalDate startDate, LocalDate endDate) {
        if (startDate != null && endDate != null && startDate.isAfter(endDate)) {
            throw new BusinessException("startDate não pode ser maior que endDate.");
        }
    }

    private String normalize(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        return value.trim().toLowerCase(Locale.ROOT);
    }

    private void apply(FinanceTransaction financeTransaction, FinanceTransactionRequest request) {
        financeTransaction.setType(request.type());
        financeTransaction.setDescription(request.description().trim());
        financeTransaction.setCategory(request.category().trim());
        financeTransaction.setPaymentMethod(request.paymentMethod().trim());
        financeTransaction.setAmount(request.amount());
        financeTransaction.setTransactionDate(request.transactionDate());
        financeTransaction.setNotes(request.notes());
    }
}
