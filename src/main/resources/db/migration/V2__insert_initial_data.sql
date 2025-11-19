-- Insert usuarios (senha: senha123)
-- BCrypt hash gerado com strength 12
INSERT INTO usuario (nome, email, senha, departamento, ativo, created_at) VALUES
('João Silva', 'joao.silva@empresa.com', '$2a$12$LQv3c1yqBWVHxkd0LHAkCOYz6TtxMQJqhN8/LewY5gyg0zXvRQ7Yi', 'TI', true, NOW()),
('Maria Santos', 'maria.santos@empresa.com', '$2a$12$LQv3c1yqBWVHxkd0LHAkCOYz6TtxMQJqhN8/LewY5gyg0zXvRQ7Yi', 'FINANCEIRO', true, NOW()),
('Pedro Costa', 'pedro.costa@empresa.com', '$2a$12$LQv3c1yqBWVHxkd0LHAkCOYz6TtxMQJqhN8/LewY5gyg0zXvRQ7Yi', 'RH', true, NOW()),
('Ana Oliveira', 'ana.oliveira@empresa.com', '$2a$12$LQv3c1yqBWVHxkd0LHAkCOYz6TtxMQJqhN8/LewY5gyg0zXvRQ7Yi', 'OPERACOES', true, NOW()),
('Carlos Souza', 'carlos.souza@empresa.com', '$2a$12$LQv3c1yqBWVHxkd0LHAkCOYz6TtxMQJqhN8/LewY5gyg0zXvRQ7Yi', 'OUTROS', true, NOW());

-- Insert modulos
INSERT INTO modulo (nome, descricao, ativo, created_at) VALUES
('Portal do Colaborador', 'Portal de acesso geral para todos os colaboradores', true, NOW()),
('Relatórios Gerenciais', 'Visualização de relatórios e dashboards gerenciais', true, NOW()),
('Gestão Financeira', 'Sistema de gestão financeira completa', true, NOW()),
('Aprovador Financeiro', 'Módulo para aprovação de transações financeiras', true, NOW()),
('Solicitante Financeiro', 'Módulo para solicitação de transações financeiras', true, NOW()),
('Administrador RH', 'Administração completa de recursos humanos', true, NOW()),
('Colaborador RH', 'Acesso básico para colaboradores do RH', true, NOW()),
('Gestão de Estoque', 'Controle de estoque e inventário', true, NOW()),
('Compras', 'Sistema de gestão de compras e fornecedores', true, NOW()),
('Auditoria', 'Módulo de auditoria e compliance (exclusivo TI)', true, NOW());

-- Insert modulo_departamento (Regras de compatibilidade)
-- Portal e Relatórios: Todos os departamentos
INSERT INTO modulo_departamento (modulo_id, departamento) VALUES
(1, 'TI'), (1, 'FINANCEIRO'), (1, 'RH'), (1, 'OPERACOES'), (1, 'OUTROS'),
(2, 'TI'), (2, 'FINANCEIRO'), (2, 'RH'), (2, 'OPERACOES'), (2, 'OUTROS');

-- Gestão Financeira, Aprovador e Solicitante: Financeiro e TI
INSERT INTO modulo_departamento (modulo_id, departamento) VALUES
(3, 'TI'), (3, 'FINANCEIRO'),
(4, 'TI'), (4, 'FINANCEIRO'),
(5, 'TI'), (5, 'FINANCEIRO');

-- Administrador RH e Colaborador RH: RH e TI
INSERT INTO modulo_departamento (modulo_id, departamento) VALUES
(6, 'TI'), (6, 'RH'),
(7, 'TI'), (7, 'RH');

-- Gestão de Estoque e Compras: Operações e TI
INSERT INTO modulo_departamento (modulo_id, departamento) VALUES
(8, 'TI'), (8, 'OPERACOES'),
(9, 'TI'), (9, 'OPERACOES');

-- Auditoria: Apenas TI
INSERT INTO modulo_departamento (modulo_id, departamento) VALUES
(10, 'TI');

-- Insert modulo_incompativel (Módulos mutuamente exclusivos)
-- Aprovador Financeiro (4) <-> Solicitante Financeiro (5)
INSERT INTO modulo_incompativel (modulo_id, modulo_incompativel_id) VALUES
(4, 5), (5, 4);

-- Administrador RH (6) <-> Colaborador RH (7)
INSERT INTO modulo_incompativel (modulo_id, modulo_incompativel_id) VALUES
(6, 7), (7, 6);

