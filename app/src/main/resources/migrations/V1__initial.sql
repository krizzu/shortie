CREATE TABLE IF NOT EXISTS urls
(
    id            BIGSERIAL PRIMARY KEY,
    short_url     VARCHAR(10)                            NOT NULL,
    original_url  VARCHAR(2048)                          NOT NULL,
    password_hash VARCHAR(128) DEFAULT NULL              NULL,
    expiry_date   TIMESTAMP    DEFAULT NULL              NULL,
    created_at    TIMESTAMP    DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updated_at    TIMESTAMP                              NOT NULL
);
ALTER TABLE urls
    ADD CONSTRAINT urls_short_url_unique UNIQUE (short_url);
CREATE SEQUENCE IF NOT EXISTS urls_id_seq START WITH 1 MINVALUE 1 MAXVALUE 9223372036854775807;
