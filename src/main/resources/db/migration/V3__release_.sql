---------------------CREATE-TABLE-churches----------------------
CREATE TABLE if NOT EXISTS churches (
    id bigserial PRIMARY KEY,
    name VARCHAR(150) NOT NULL,
    cnpj VARCHAR(14) UNIQUE,
    city VARCHAR(80),
    state VARCHAR(2),
    created_at TIMESTAMP NOT NULL DEFAULT now()
    );
---------------------ALTERATION-TABLE-users----------------------
ALTER TABLE users
    ADD COLUMN if NOT EXISTS church_id bigint;
---------------------CREATE-CHURCH-DEFAULT-----------------------
INSERT INTO churches (name, cnpj, city, state)
VALUES ('Assembleia de Deus Missão SubSede Jardim Todos os Santos','57166972000178' ,'Senador Canedo', 'GO')
    ON conflict do nothing;
---------------------ADJUST-users--------------------------------
UPDATE users SET church_id = 1 WHERE church_id IS NULL;

ALTER TABLE users
    ALTER COLUMN church_id SET NOT NULL;

ALTER TABLE users
    ADD CONSTRAINT fk_users_church
        FOREIGN KEY (church_id) REFERENCES churches(id);
