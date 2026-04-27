package br.com.ekklesia.ekklesia_admin_api.finance;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.time.LocalDate;

public record FinanceTransactionRequest(
        @NotNull FinanceTransactionType type,
        @NotBlank @Size(max = 255) String description,
        @NotBlank @Size(max = 100) String category,
        @NotBlank @Size(max = 50) String paymentMethod,
        @NotNull @DecimalMin(value = "0.01") BigDecimal amount,
        @NotNull LocalDate transactionDate,
        @Size(max = 4000) String notes
) {
}
