
CREATE TABLE users (
                       id UUID PRIMARY KEY,
                       email VARCHAR(255) NOT NULL UNIQUE,
                       age INTEGER,
                       role VARCHAR(50),
                       password_hash VARCHAR(255)
);

CREATE TABLE locations (
                           id UUID PRIMARY KEY,
                           name VARCHAR(255),
                           address TEXT,
                           capacity BIGINT,
                           description TEXT
);

CREATE TABLE events (
                        id UUID PRIMARY KEY,
                        name VARCHAR(255) NOT NULL,
                        owner_id UUID NOT NULL,
                        max_places INTEGER NOT NULL,
                        date TIMESTAMP NOT NULL,
                        cost INTEGER NOT NULL,
                        duration INTEGER NOT NULL,
                        location_id UUID NOT NULL,
                        status VARCHAR(50) NOT NULL CHECK (status IN ('WAIT_START', 'STARTED', 'CANCELLED', 'FINISHED')),
                        FOREIGN KEY (owner_id) REFERENCES users(id),
                        FOREIGN KEY (location_id) REFERENCES locations(id)
);

CREATE TABLE registrations (
                               id UUID PRIMARY KEY,
                               user_id UUID NOT NULL,
                               FOREIGN KEY (user_id) REFERENCES users(id)
);
