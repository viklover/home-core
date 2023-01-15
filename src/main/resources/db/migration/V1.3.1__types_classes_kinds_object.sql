CREATE TABLE object_type
(
    type VARCHAR(15) PRIMARY KEY
);

CREATE TABLE object_class
(
    class VARCHAR(40) PRIMARY KEY NOT NULL,
    type  VARCHAR(15)             NOT NULL,
    FOREIGN KEY (type) references object_type (type)
);

CREATE TABLE object_kind
(
    kind  VARCHAR(80),
    class VARCHAR(40) NOT NULL,
    FOREIGN KEY (class) references object_class (class)
);

INSERT INTO object_type
VALUES ('t_object'),
       ('v_object');

INSERT INTO object_class
VALUES ('input', 't_object'),
       ('output', 't_object'),
       ('trigger', 't_object'),
       ('onewire', 'v_object');

INSERT INTO object_kind
VALUES ('default', 'input'),
       ('default', 'output'),
       ('default', 'trigger'),
       ('default', 'onewire'),
       ('serial', 'onewire');