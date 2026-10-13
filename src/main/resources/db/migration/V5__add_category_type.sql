ALTER TABLE categories
    ADD COLUMN type VARCHAR(30) NOT NULL DEFAULT 'OTHER';

ALTER TABLE categories
    ADD CONSTRAINT chk_categories_type
        CHECK (
            type IN (
                     'DRINKS',
                     'DESSERTS',
                     'BAKERY',
                     'FOOD',
                     'OTHER'
                )
            );

CREATE INDEX idx_categories_type
    ON categories(type);