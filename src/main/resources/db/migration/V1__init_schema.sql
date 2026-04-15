CREATE TABLE churches (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(150) NOT NULL,
    cnpj VARCHAR(14) NOT NULL UNIQUE,
    city VARCHAR(80),
    state VARCHAR(2),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE ek_role (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(50) NOT NULL UNIQUE
);

CREATE TABLE ek_persona (
    id BIGSERIAL PRIMARY KEY,
    church_id BIGINT,
    persona_type VARCHAR(50),
    tax_id VARCHAR(14),
    name VARCHAR(200),
    birth_date DATE,
    marital_status VARCHAR(50),
    phone_contact VARCHAR(20),
    email_contact VARCHAR(150),
    address VARCHAR(255),
    CONSTRAINT fk_ek_persona_church
        FOREIGN KEY (church_id) REFERENCES churches(id)
);

CREATE TABLE ek_users (
    id BIGSERIAL PRIMARY KEY,
    persona_id BIGINT NOT NULL UNIQUE,
    email VARCHAR(150) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    CONSTRAINT fk_ek_users_persona
        FOREIGN KEY (persona_id) REFERENCES ek_persona(id)
);

CREATE TABLE ek_user_role (
    user_id BIGINT NOT NULL,
    role_id BIGINT NOT NULL,
    PRIMARY KEY (user_id, role_id),
    CONSTRAINT fk_ek_user_role_user
        FOREIGN KEY (user_id) REFERENCES ek_users(id) ON DELETE CASCADE,
    CONSTRAINT fk_ek_user_role_role
        FOREIGN KEY (role_id) REFERENCES ek_role(id) ON DELETE CASCADE
);

CREATE TABLE ek_member (
    id BIGSERIAL PRIMARY KEY,
    persona_id BIGINT NOT NULL UNIQUE,
    membership_date DATE,
    baptism_date DATE,
    is_baptized BOOLEAN,
    ministry VARCHAR(50),
    status_member VARCHAR(50),
    notes TEXT,
    CONSTRAINT fk_ek_member_persona
        FOREIGN KEY (persona_id) REFERENCES ek_persona(id)
);

CREATE TABLE audit_log (
    id BIGSERIAL PRIMARY KEY,
    entity_name VARCHAR(100) NOT NULL,
    entity_id INTEGER NOT NULL,
    action VARCHAR(20) NOT NULL,
    username VARCHAR(150) NOT NULL,
    details TEXT,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);
