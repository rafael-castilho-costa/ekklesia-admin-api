package br.com.ekklesia.ekklesia_admin_api.finance;

import java.math.BigDecimal;
import java.time.LocalDate;

public record FinanceTransactionResponse(
        Integer id,
        FinanceTransactionType type,
        String description,
        String category,
        String paymentMethod,
        BigDecimal amount,
        LocalDate transactionDate,
        String notes
) {

    public static FinanceTransactionResponse from(FinanceTransaction financeTransaction) {
        return new FinanceTransactionResponse(
                financeTransaction.getId(),
                financeTransaction.getType(),
                financeTransaction.getDescription(),
                financeTransaction.getCategory(),
                financeTransaction.getPaymentMethod(),
                financeTransaction.getAmount(),
                financeTransaction.getTransactionDate(),
                financeTransaction.getNotes()
        );
    }
}
