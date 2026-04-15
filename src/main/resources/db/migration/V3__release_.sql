CREATE INDEX idx_ek_users_email ON ek_users(email);
CREATE INDEX idx_ek_persona_church_id ON ek_persona(church_id);
CREATE INDEX idx_audit_log_entity_name ON audit_log(entity_name);
CREATE INDEX idx_audit_log_created_at ON audit_log(created_at);
