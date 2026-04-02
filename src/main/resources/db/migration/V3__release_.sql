---------------------CREATE-TABLE-churches----------------------
CREATE TABLE IF NOT EXISTS churches (
                                        id BIGSERIAL PRIMARY KEY,
                                        name VARCHAR(150) NOT NULL,
    cnpj VARCHAR(14) UNIQUE,
    city VARCHAR(80),
    state VARCHAR(2),
    created_at TIMESTAMP NOT NULL DEFAULT NOW()
    );

---------------------ALTERATION-TABLE-users----------------------
ALTER TABLE users
    ADD COLUMN IF NOT EXISTS church_id BIGINT;

---------------------CREATE-CHURCH-DEFAULT-----------------------
INSERT INTO churches (name, cnpj, city, state)
VALUES ('Assembleia de Deus Missão SubSede Jardim Todos os Santos', '57166972000178', 'Senador Canedo', 'GO')
    ON CONFLICT DO NOTHING;

---------------------ADJUST-users--------------------------------
UPDATE users
SET church_id = 1
WHERE church_id IS NULL;

ALTER TABLE users
    ALTER COLUMN church_id SET NOT NULL;

DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1
        FROM information_schema.table_constraints
        WHERE constraint_name = 'fk_users_church'
          AND table_name = 'users'
    ) THEN
ALTER TABLE users
    ADD CONSTRAINT fk_users_church
        FOREIGN KEY (church_id) REFERENCES churches(id);
END IF;
END $$;

---------------CREATE-TABLE-audit_log---------------------------
CREATE TABLE IF NOT EXISTS audit_log (
                                         id BIGSERIAL PRIMARY KEY,
                                         entity_name VARCHAR(100) NOT NULL,
    entity_id INTEGER NOT NULL,
    action VARCHAR(20) NOT NULL,
    username VARCHAR(150) NOT NULL,
    details TEXT,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
    );
