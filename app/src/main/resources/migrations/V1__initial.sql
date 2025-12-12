CREATE TABLE IF NOT EXISTS urls
(
    id            BIGSERIAL PRIMARY KEY,
    short_code    VARCHAR(10)                            NOT NULL,
    original_url  VARCHAR(2048)                          NOT NULL,
    password_hash VARCHAR(255) DEFAULT NULL              NULL,
    expiry_date   TIMESTAMP    DEFAULT NULL              NULL,
    created_at    TIMESTAMP    DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updated_at    TIMESTAMP                              NOT NULL
);
ALTER TABLE urls
    ADD CONSTRAINT urls_short_code_unique UNIQUE (short_code);
CREATE TABLE IF NOT EXISTS users
(
    id         BIGSERIAL PRIMARY KEY,
    "name"     VARCHAR(64)                         NOT NULL,
    "password" VARCHAR(255)                        NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updated_at TIMESTAMP                           NOT NULL
);
ALTER TABLE users
    ADD CONSTRAINT users_name_unique UNIQUE ("name");
CREATE SEQUENCE IF NOT EXISTS urls_id_seq START WITH 1 MINVALUE 1 MAXVALUE 9223372036854775807;
CREATE SEQUENCE IF NOT EXISTS users_id_seq START WITH 1 MINVALUE 1 MAXVALUE 9223372036854775807;
CREATE SEQUENCE IF NOT EXISTS global_counter_seq START WITH 1 MINVALUE 1 MAXVALUE 9223372036854775807;
