CREATE TABLE ek_finance_transaction (
    id BIGSERIAL PRIMARY KEY,
    church_id BIGINT NOT NULL,
    type VARCHAR(20) NOT NULL,
    description VARCHAR(255) NOT NULL,
    category VARCHAR(100) NOT NULL,
    payment_method VARCHAR(50) NOT NULL,
    amount NUMERIC(15, 2) NOT NULL,
    transaction_date DATE NOT NULL,
    notes TEXT,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_ek_finance_transaction_church
        FOREIGN KEY (church_id) REFERENCES churches(id)
);

CREATE INDEX idx_ek_finance_transaction_church_id ON ek_finance_transaction(church_id);
CREATE INDEX idx_ek_finance_transaction_date ON ek_finance_transaction(transaction_date);
