CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

DROP TABLE IF EXISTS users_roles, users, roles_permissions, roles, permission CASCADE;

CREATE TABLE permission (
    name        VARCHAR(255) NOT NULL PRIMARY KEY,
    description VARCHAR(255)
);

CREATE TABLE roles (
    name        VARCHAR(255) NOT NULL PRIMARY KEY,
    description VARCHAR(255)
);

CREATE TABLE roles_permissions (
    role_name        VARCHAR(255) NOT NULL,
    permissions_name VARCHAR(255) NOT NULL,
    PRIMARY KEY (role_name, permissions_name),
    FOREIGN KEY (role_name) REFERENCES roles(name),
    FOREIGN KEY (permissions_name) REFERENCES permission(name)
);

CREATE TABLE users (
    user_id       UUID DEFAULT gen_random_uuid() PRIMARY KEY,
    dob           DATE,
    first_name    VARCHAR(50),
    last_name     VARCHAR(50),
    password      VARCHAR(255) NOT NULL,
    token_version BIGINT DEFAULT 0 NOT NULL,
    email         VARCHAR(255) UNIQUE
);

CREATE TABLE users_roles (
    user_id UUID NOT NULL,
    roles_name   VARCHAR(255) NOT NULL,
    PRIMARY KEY (user_user_id, roles_name),
    FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE,
    FOREIGN KEY (roles_name) REFERENCES roles(name)
);

ALTER TABLE permission OWNER TO postgres;
ALTER TABLE roles OWNER TO postgres;
ALTER TABLE roles_permissions OWNER TO postgres;
ALTER TABLE users OWNER TO postgres;
ALTER TABLE users_roles OWNER TO postgres;
