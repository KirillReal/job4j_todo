create table users (
                       id serial primary key,
                       name text NOT NULL,
                       email text unique NOT NULL,
                       password text NOT NULL
);

ALTER TABLE item ADD COLUMN user_id INT REFERENCES users(id) ON DELETE CASCADE;