CREATE TABLE client_results (
    id BIGSERIAL PRIMARY KEY,
    client_name VARCHAR(100) NOT NULL,
    age INT,
    diagnosis VARCHAR(255),
    weight_loss_kg DECIMAL(5,2),
    duration_months INT,
    description TEXT,
    photo_filename VARCHAR(255),
    display_order INT DEFAULT 0,
    created_at TIMESTAMP DEFAULT NOW()
);

CREATE TABLE certificates (
    id BIGSERIAL PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    issuer VARCHAR(255),
    year INT,
    image_filename VARCHAR(255),
    display_order INT DEFAULT 0
);

CREATE TABLE admin_users (
    id BIGSERIAL PRIMARY KEY,
    username VARCHAR(50) UNIQUE NOT NULL,
    password_hash VARCHAR(255) NOT NULL
);

-- Default admin password: admin123 (change after first login)
INSERT INTO admin_users (username, password_hash)
VALUES ('admin', '$2b$10$jtZ6ce5XJFRfRmTHlDR/zOfTLai48mn4fyPsciDCrhHabkzPD8Id.');
