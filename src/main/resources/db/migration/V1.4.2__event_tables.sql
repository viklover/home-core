CREATE TABLE t_object_event
(
    id                BIGSERIAL PRIMARY KEY,
    center_id         BIGINT NOT NULL,
    center_session_id BIGINT NOT NULL,
    core_session_id   BIGINT NOT NULL,
    time              TIMESTAMP,
    object_id         BIGINT NOT NULL,
    event_type        BIGINT NOT NULL,
    score             BIGINT NOT NULL,
    FOREIGN KEY (object_id) references object (id),
    FOREIGN KEY (event_type) references t_object_event_type (id),
    FOREIGN KEY (core_session_id) references session (id),
    FOREIGN KEY (center_id) references center (id)
);

CREATE TABLE v_object_event
(
    id         BIGSERIAL PRIMARY KEY,
    time       TIMESTAMP,
    object_id  BIGINT           NOT NULL,
    event_type BIGINT           NOT NULL,
    value      DOUBLE PRECISION NOT NULL,
    FOREIGN KEY (object_id) references object (id),
    FOREIGN KEY (event_type) references v_object_event_type (id)
);