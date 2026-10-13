ALTER TABLE users
    ADD COLUMN name VARCHAR(100) NOT NULL DEFAULT '';

UPDATE users
SET name = 'Пользователь' || id
WHERE length(trim(name)) = 0;

ALTER TABLE users
    ADD CONSTRAINT chk_users_name
        CHECK (length(trim(name)) > 0);