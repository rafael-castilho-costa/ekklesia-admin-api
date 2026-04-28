ALTER TABLE ek_users
    ADD COLUMN scope VARCHAR(20);

ALTER TABLE ek_users
    ADD COLUMN church_id BIGINT;

UPDATE ek_users u
SET church_id = p.church_id
FROM ek_persona p
WHERE p.id = u.persona_id;

UPDATE ek_users
SET scope = 'TENANT'
WHERE scope IS NULL;

ALTER TABLE ek_users
    ALTER COLUMN scope SET NOT NULL;

ALTER TABLE ek_users
    ADD CONSTRAINT fk_ek_users_church
        FOREIGN KEY (church_id) REFERENCES churches(id);

CREATE INDEX idx_ek_users_church_id ON ek_users(church_id);

INSERT INTO ek_role (name)
VALUES ('ROLE_ADMIN_MASTER')
ON CONFLICT (name) DO NOTHING;
