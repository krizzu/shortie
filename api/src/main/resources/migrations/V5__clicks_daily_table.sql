CREATE TABLE IF NOT EXISTS clicks_daily
(
    id          BIGSERIAL PRIMARY KEY,
    short_code  VARCHAR(30)                         NOT NULL,
    click_date  DATE                                NOT NULL,
    click_count BIGINT                              NOT NULL,
    created_at  TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updated_at  TIMESTAMP                           NOT NULL,
    CONSTRAINT fk_clicks_daily_short_code__short_code FOREIGN KEY (short_code) REFERENCES urls (short_code) ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT check_clicks_daily_0 CHECK (click_count >= 0)
);
ALTER TABLE clicks_daily
    ADD CONSTRAINT clicks_short_code_date_unique UNIQUE (short_code, click_date);
CREATE SEQUENCE IF NOT EXISTS clicks_daily_id_seq START WITH 1 MINVALUE 1 MAXVALUE 9223372036854775807;
