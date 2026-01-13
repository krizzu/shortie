ALTER TABLE urls
    ADD total_clicks BIGINT DEFAULT 0 NOT NULL;
ALTER TABLE urls
    ADD last_redirect TIMESTAMP NULL;

