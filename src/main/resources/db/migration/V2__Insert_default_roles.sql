-- Seed default roles for application
INSERT INTO roles(name)
VALUES ('ROLE_USER'),
       ('ROLE_ADMIN')
ON CONFLICT (name) DO NOTHING;


