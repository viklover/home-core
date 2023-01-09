CREATE TABLE object
(
    id               BIGSERIAL PRIMARY KEY,
    name             VARCHAR(50) NOT NULL,
    short_name       VARCHAR(10) NOT NULL,
    type             VARCHAR(15) NOT NULL,
    class            VARCHAR(40) NOT NULL,
    kind             VARCHAR(80) DEFAULT 'default',
    include_in_stats BOOLEAN     DEFAULT TRUE,
    created_at       TIMESTAMP   DEFAULT NOW(),
    updated_at       TIMESTAMP   DEFAULT NOW(),
    removed          BOOLEAN     DEFAULT FALSE,
    FOREIGN KEY (type) REFERENCES object_type (type),
    FOREIGN KEY (class) REFERENCES object_class (class)
);

CREATE TABLE t_object
(
    object_id BIGINT,
    opposite  BOOLEAN DEFAULT FALSE,
    resume    FLOAT   DEFAULT 0,
    FOREIGN KEY (object_id) REFERENCES object (id) ON DELETE CASCADE
);

CREATE TABLE v_object
(
    object_id BIGINT,
    min       FLOAT,
    max       FLOAT,
    variance  FLOAT,
    FOREIGN KEY (object_id) REFERENCES object (id) ON DELETE CASCADE
);

CREATE TABLE input
(
    object_id BIGINT,
    pin       INTEGER NOT NULL,
    FOREIGN KEY (object_id) REFERENCES object (id) ON DELETE CASCADE
);

CREATE TABLE output
(
    object_id BIGINT,
    pin       INTEGER NOT NULL,
    FOREIGN KEY (object_id) REFERENCES object (id) ON DELETE CASCADE
);

CREATE TABLE trigger
(
    object_id BIGINT,
    FOREIGN KEY (object_id) REFERENCES object (id) ON DELETE CASCADE
);

CREATE TABLE onewire
(
    object_id     BIGSERIAL,
    serial_number VARCHAR(22) NOT NULL,
    FOREIGN KEY (object_id) REFERENCES object (id) ON DELETE CASCADE
);

CREATE TABLE onewire__serial
(
    object_id BIGINT,
    path      TEXT NOT NULL,
    FOREIGN KEY (object_id) REFERENCES object (id) ON DELETE CASCADE
);
