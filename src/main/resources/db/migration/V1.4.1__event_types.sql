CREATE TABLE t_object_event_type
(
    id   BIGSERIAL PRIMARY KEY,
    name TEXT
);

CREATE TABLE v_object_event_type
(
    id   BIGSERIAL PRIMARY KEY,
    name TEXT
);

INSERT INTO t_object_event_type (name)
VALUES ('ON'),
       ('OFF');

INSERT INTO v_object_event_type (name)
VALUES ('READ');