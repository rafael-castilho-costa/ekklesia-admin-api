CREATE TABLE church (
                        id UUID PRIMARY KEY,
                        name VARCHAR(150) NOT NULL,
                        created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE users (
                       id UUID PRIMARY KEY,
                       church_id UUID NOT NULL,
                       name VARCHAR(150) NOT NULL,
                       email VARCHAR(150) NOT NULL,
                       password VARCHAR(255) NOT NULL,
                       role VARCHAR(50) NOT NULL,
                       created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                       CONSTRAINT fk_user_church FOREIGN KEY (church_id) REFERENCES church(id)
);
