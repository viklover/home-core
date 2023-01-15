CREATE TABLE session
(
    id         BIGSERIAL PRIMARY KEY,
    started_at TIMESTAMP DEFAULT NOW()
);