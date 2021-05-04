CREATE TABLE item (
                      id SERIAL PRIMARY KEY,
                      description TEXT NOT NULL,
                      created TIMESTAMP NOT NULL DEFAULT now(),
                      done BOOLEAN NOT NULL DEFAULT false
);