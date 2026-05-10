CREATE TABLE IF NOT EXISTS families (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    family_name VARCHAR(120) NOT NULL,
    join_code VARCHAR(120) NOT NULL UNIQUE
);
CREATE TABLE IF NOT EXISTS contacts (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    family_id BIGINT NOT NULL,
    first_name VARCHAR(80) NOT NULL,
    surname VARCHAR(80),
    phone VARCHAR(20) NOT NULL,
    dob DATE NOT NULL,
    anniversary DATE,
    CONSTRAINT fk_family FOREIGN KEY (family_id) REFERENCES families(id)
);