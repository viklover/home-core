CREATE TABLE center
(
    id   BIGSERIAL PRIMARY KEY,
    host INET NOT NULL,
    name TEXT NOT NULL
);