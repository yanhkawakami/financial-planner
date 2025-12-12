INSERT INTO tb_category (name) VALUES ('Alimentação');
INSERT INTO tb_category (name) VALUES ('Transporte');
INSERT INTO tb_category (name) VALUES ('Moradia');
INSERT INTO tb_category (name) VALUES ('Entretenimento');

INSERT INTO tb_role (authority) VALUES ('ROLE_ADMIN');
INSERT INTO tb_role (authority) VALUES ('ROLE_USER');

-- Users
-- Admin: senha "123456"
INSERT INTO tb_user (name, email, phone, password) VALUES ('Admin', 'admin@email.com', '11999999999', '$2a$10$NYFZ/8WaQ3Qb6FCs.00jce4nxX9w7AkgWVsQCG6oUwTAcZqP9Flqu');
-- User: senha "123456"
INSERT INTO tb_user (name, email, phone, password) VALUES ('User', 'user@email.com', '11888888888', '$2a$10$NYFZ/8WaQ3Qb6FCs.00jce4nxX9w7AkgWVsQCG6oUwTAcZqP9Flqu');

-- User Roles
-- Admin tem ROLE_ADMIN e ROLE_USER
INSERT INTO tb_user_role (user_id, role_id) VALUES (1, 1);
INSERT INTO tb_user_role (user_id, role_id) VALUES (1, 2);
-- User tem apenas ROLE_USER
INSERT INTO tb_user_role (user_id, role_id) VALUES (2, 2);
