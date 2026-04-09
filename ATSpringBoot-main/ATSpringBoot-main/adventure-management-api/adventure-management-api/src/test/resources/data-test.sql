
INSERT INTO audit.organizacoes (id, nome, ativo) VALUES (1, 'Guilda dos Heróis', true);
INSERT INTO audit.organizacoes (id, nome, ativo) VALUES (2, 'Ordem dos Magos', true);

INSERT INTO audit.usuarios (id, organizacao_id, nome, email, senha_hash, status) VALUES (1, 1, 'Admin Herói', 'admin@guilda.com', '$2a$10$hash1', 'ATIVO');
INSERT INTO audit.usuarios (id, organizacao_id, nome, email, senha_hash, status) VALUES (2, 1, 'User Herói', 'user@guilda.com', '$2a$10$hash2', 'ATIVO');
INSERT INTO audit.usuarios (id, organizacao_id, nome, email, senha_hash, status) VALUES (3, 2, 'Admin Mago', 'admin@magos.com', '$2a$10$hash3', 'ATIVO');

INSERT INTO audit.permissions (id, code, descricao) VALUES (1, 'READ_USERS', 'Listar usuários');
INSERT INTO audit.permissions (id, code, descricao) VALUES (2, 'WRITE_USERS', 'Criar/editar usuários');
INSERT INTO audit.permissions (id, code, descricao) VALUES (3, 'DELETE_USERS', 'Remover usuários');
INSERT INTO audit.permissions (id, code, descricao) VALUES (4, 'READ_ADVENTURERS', 'Listar aventureiros');

INSERT INTO audit.roles (id, organizacao_id, nome, descricao) VALUES (1, 1, 'ADMIN', 'Administrador da Guilda');
INSERT INTO audit.roles (id, organizacao_id, nome, descricao) VALUES (2, 1, 'MEMBRO', 'Membro da Guilda');
INSERT INTO audit.roles (id, organizacao_id, nome, descricao) VALUES (3, 2, 'ADMIN', 'Administrador da Ordem');

INSERT INTO audit.user_roles (usuario_id, role_id) VALUES (1, 1);
INSERT INTO audit.user_roles (usuario_id, role_id) VALUES (1, 2);
INSERT INTO audit.user_roles (usuario_id, role_id) VALUES (2, 2);
INSERT INTO audit.user_roles (usuario_id, role_id) VALUES (3, 3);

INSERT INTO audit.role_permissions (role_id, permission_id) VALUES (1, 1);
INSERT INTO audit.role_permissions (role_id, permission_id) VALUES (1, 2);
INSERT INTO audit.role_permissions (role_id, permission_id) VALUES (1, 3);
INSERT INTO audit.role_permissions (role_id, permission_id) VALUES (1, 4);
INSERT INTO audit.role_permissions (role_id, permission_id) VALUES (2, 1);
INSERT INTO audit.role_permissions (role_id, permission_id) VALUES (2, 4);

INSERT INTO audit.api_keys (id, organizacao_id, nome, key_hash, ativo) VALUES (1, 1, 'API Principal', '$2a$10$keyhash1', true);


INSERT INTO aventura.aventureiros (id, organizacao_id, usuario_id, nome, classe, nivel, ativo, created_at, updated_at)
VALUES (1, 1, 1, 'Aragorn', 'GUERREIRO', 10, true, TIMESTAMP '2025-01-01 10:00:00+00:00', TIMESTAMP '2025-01-01 10:00:00+00:00');

INSERT INTO aventura.aventureiros (id, organizacao_id, usuario_id, nome, classe, nivel, ativo, created_at, updated_at)
VALUES (2, 1, 1, 'Gandalf', 'MAGO', 20, true, TIMESTAMP '2025-01-02 10:00:00+00:00', TIMESTAMP '2025-01-02 10:00:00+00:00');

INSERT INTO aventura.aventureiros (id, organizacao_id, usuario_id, nome, classe, nivel, ativo, created_at, updated_at)
VALUES (3, 1, 1, 'Legolas', 'ARQUEIRO', 15, true, TIMESTAMP '2025-01-03 10:00:00+00:00', TIMESTAMP '2025-01-03 10:00:00+00:00');

INSERT INTO aventura.aventureiros (id, organizacao_id, usuario_id, nome, classe, nivel, ativo, created_at, updated_at)
VALUES (4, 1, 2, 'Gimli', 'GUERREIRO', 12, false, TIMESTAMP '2025-01-04 10:00:00+00:00', TIMESTAMP '2025-06-01 10:00:00+00:00');

INSERT INTO aventura.aventureiros (id, organizacao_id, usuario_id, nome, classe, nivel, ativo, created_at, updated_at)
VALUES (5, 2, 3, 'Saruman', 'MAGO', 25, true, TIMESTAMP '2025-01-05 10:00:00+00:00', TIMESTAMP '2025-01-05 10:00:00+00:00');

INSERT INTO aventura.companheiros (aventureiro_id, nome, especie, indice_lealdade) VALUES (1, 'Brego', 'LOBO', 95);
INSERT INTO aventura.companheiros (aventureiro_id, nome, especie, indice_lealdade) VALUES (3, 'Arod', 'AGUIA', 88);

INSERT INTO aventura.missoes (id, organizacao_id, titulo, nivel_perigo, status, created_at, data_inicio, data_termino)
VALUES (1, 1, 'A Sociedade do Anel', 'EXTREMO', 'CONCLUIDA', TIMESTAMP '2025-02-01 08:00:00+00:00', TIMESTAMP '2025-02-01 09:00:00+00:00', TIMESTAMP '2025-06-01 18:00:00+00:00');

INSERT INTO aventura.missoes (id, organizacao_id, titulo, nivel_perigo, status, created_at, data_inicio)
VALUES (2, 1, 'As Duas Torres', 'ALTO', 'EM_ANDAMENTO', TIMESTAMP '2025-06-01 08:00:00+00:00', TIMESTAMP '2025-06-15 09:00:00+00:00');

INSERT INTO aventura.missoes (id, organizacao_id, titulo, nivel_perigo, status, created_at)
VALUES (3, 1, 'O Retorno do Rei', 'EXTREMO', 'PLANEJADA', TIMESTAMP '2025-07-01 08:00:00+00:00');

INSERT INTO aventura.missoes (id, organizacao_id, titulo, nivel_perigo, status, created_at)
VALUES (4, 1, 'Missão Cancelada', 'BAIXO', 'CANCELADA', TIMESTAMP '2025-03-01 08:00:00+00:00');

INSERT INTO aventura.missoes (id, organizacao_id, titulo, nivel_perigo, status, created_at)
VALUES (5, 2, 'Busca pelo Palantir', 'MEDIO', 'PLANEJADA', TIMESTAMP '2025-04-01 08:00:00+00:00');

INSERT INTO aventura.participacoes_missao (id, missao_id, aventureiro_id, papel, recompensa_ouro, destaque, data_registro)
VALUES (1, 1, 1, 'LIDER', 500.00, true, TIMESTAMP '2025-02-01 09:00:00+00:00');

INSERT INTO aventura.participacoes_missao (id, missao_id, aventureiro_id, papel, recompensa_ouro, destaque, data_registro)
VALUES (2, 1, 2, 'SUPORTE', 300.00, false, TIMESTAMP '2025-02-01 09:00:00+00:00');

INSERT INTO aventura.participacoes_missao (id, missao_id, aventureiro_id, papel, recompensa_ouro, destaque, data_registro)
VALUES (3, 1, 3, 'ATACANTE', 400.00, true, TIMESTAMP '2025-02-01 09:00:00+00:00');

INSERT INTO aventura.participacoes_missao (id, missao_id, aventureiro_id, papel, recompensa_ouro, destaque, data_registro)
VALUES (4, 2, 1, 'LIDER', 200.00, false, TIMESTAMP '2025-06-15 10:00:00+00:00');

INSERT INTO aventura.participacoes_missao (id, missao_id, aventureiro_id, papel, recompensa_ouro, destaque, data_registro)
VALUES (5, 2, 3, 'BATEDOR', 150.00, true, TIMESTAMP '2025-06-15 10:00:00+00:00');
