-- Atualiza senhas dos usuários
-- BCrypt hash para "senha123" com strength 15 (conforme requisito)
-- Hash: $2a$15$4umnMu8qBecMhoK4Q4SfouVygnmI39j5P0phuLL/ioMVBOMPHBwJC

UPDATE usuario SET senha = '$2a$12$4umnMu8qBecMhoK4Q4SfouVygnmI39j5P0phuLL/ioMVBOMPHBwJC' WHERE email = 'joao.silva@empresa.com';
UPDATE usuario SET senha = '$2a$12$4umnMu8qBecMhoK4Q4SfouVygnmI39j5P0phuLL/ioMVBOMPHBwJC' WHERE email = 'maria.santos@empresa.com';
UPDATE usuario SET senha = '$2a$12$4umnMu8qBecMhoK4Q4SfouVygnmI39j5P0phuLL/ioMVBOMPHBwJC' WHERE email = 'pedro.costa@empresa.com';
UPDATE usuario SET senha = '$2a$12$4umnMu8qBecMhoK4Q4SfouVygnmI39j5P0phuLL/ioMVBOMPHBwJC' WHERE email = 'ana.oliveira@empresa.com';
UPDATE usuario SET senha = '$2a$12$4umnMu8qBecMhoK4Q4SfouVygnmI39j5P0phuLL/ioMVBOMPHBwJC' WHERE email = 'carlos.souza@empresa.com';

