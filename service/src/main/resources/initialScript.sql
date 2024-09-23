
CREATE TABLE users (
                       id UUID PRIMARY KEY,
                       email VARCHAR(255) NOT NULL UNIQUE,
                       age INTEGER,
                       role VARCHAR(50) NOT NULL ,
                       password VARCHAR(255) NOT NULL
);

CREATE TABLE locations (
                           id UUID PRIMARY KEY,
                           name VARCHAR(255) NOT NULL,
                           address TEXT NOT NULL,
                           capacity BIGINT NOT NULL ,
                           description TEXT NOT NULL
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
                        status VARCHAR(50) NOT NULL,
                        FOREIGN KEY (owner_id) REFERENCES users(id),
                        FOREIGN KEY (location_id) REFERENCES locations(id)
);

CREATE TABLE registrations (
                               id UUID PRIMARY KEY,
                               user_id UUID NOT NULL,
                               event_id UUID NOT NULL,
                               registration_date TIMESTAMP NOT NULL,
                               FOREIGN KEY (user_id) REFERENCES users(id),
                               FOREIGN KEY (event_id) REFERENCES events(id)
);
