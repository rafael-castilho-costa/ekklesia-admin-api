INSERT INTO ek_role (name)
VALUES ('ROLE_ADMIN'), ('ROLE_SECRETARY'), ('ROLE_TREASURER'), ('ROLE_TEACHER')
ON CONFLICT (name) DO NOTHING;

INSERT INTO churches (name, cnpj, city, state)
VALUES ('Assembleia de Deus Missao Subsede Jardim Todos os Santos', '57166972000178', 'Senador Canedo', 'GO')
ON CONFLICT (cnpj) DO NOTHING;
