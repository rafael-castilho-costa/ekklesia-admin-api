package br.com.ekklesia.ekklesia_admin_api.finance;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/finance/transactions")
@RequiredArgsConstructor
public class FinanceTransactionController {

    private final FinanceTransactionService financeTransactionService;

    @GetMapping
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN_MASTER', 'ROLE_ADMIN', 'ROLE_SECRETARY', 'ROLE_TREASURER')")
    public List<FinanceTransactionResponse> list(
            @RequestParam(required = false) FinanceTransactionType type,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String paymentMethod,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate
    ) {
        return financeTransactionService.list(type, category, paymentMethod, startDate, endDate);
    }

    @PostMapping
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN_MASTER', 'ROLE_ADMIN', 'ROLE_SECRETARY', 'ROLE_TREASURER')")
    public ResponseEntity<FinanceTransactionResponse> create(@Valid @RequestBody FinanceTransactionRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(financeTransactionService.create(request));
    }
}
