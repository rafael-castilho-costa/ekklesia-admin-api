package br.com.ekklesia.ekklesia_admin_api.finance;

import br.com.ekklesia.ekklesia_admin_api.church.Church;
import br.com.ekklesia.ekklesia_admin_api.domain.vo.base.BaseDateRecordEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Getter
@Setter
@Table(name = "ek_finance_transaction")
public class FinanceTransaction extends BaseDateRecordEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "church_id", nullable = false)
    private Church church;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private FinanceTransactionType type;

    @Column(nullable = false, length = 255)
    private String description;

    @Column(nullable = false, length = 100)
    private String category;

    @Column(name = "payment_method", nullable = false, length = 50)
    private String paymentMethod;

    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal amount;

    @Column(name = "transaction_date", nullable = false)
    private LocalDate transactionDate;

    @Column(columnDefinition = "TEXT")
    private String notes;
}
