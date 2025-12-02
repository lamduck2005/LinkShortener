-- Tạo tài khoản admin mặc định
-- admin - 123456
INSERT INTO users (email, username, password_hash, is_active)
VALUES ('admin@example.com', 'admin', '$2a$10$KVMxz7.ohDp2XCB70F5DnODzS32l4QOPUX8e1OrEnOSzYPutRgK.i', TRUE)
ON CONFLICT (username) DO NOTHING;

-- Gán ROLE_ADMIN cho user 'admin'
INSERT INTO user_roles (user_id, role_id)
SELECT u.id, r.id
FROM users u
JOIN roles r ON r.name = 'ROLE_ADMIN'
WHERE u.username = 'admin'
ON CONFLICT (user_id, role_id) DO NOTHING;