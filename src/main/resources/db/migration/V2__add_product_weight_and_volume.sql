ALTER TABLE products
    ADD COLUMN weight INTEGER,
    ADD COLUMN is_drink BOOLEAN NOT NULL DEFAULT FALSE,
    ADD COLUMN volume INTEGER;

ALTER TABLE products
    ADD CONSTRAINT chk_products_weight
        CHECK (weight IS NULL OR weight > 0),
    ADD CONSTRAINT chk_products_drink_volume
        CHECK (
            (is_drink = TRUE AND volume IS NOT NULL AND volume > 0)
            OR
            (is_drink = FALSE AND volume IS NULL)
        );
