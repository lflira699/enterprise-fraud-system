-- EFS-DB-002
-- V70 - Catalog Item

CREATE TABLE catalog.catalog_item (
    catalog_item_id UUID NOT NULL DEFAULT uuidv7(),
    catalog_id UUID NOT NULL,
    item_code VARCHAR(60) NOT NULL,
    item_name VARCHAR(150) NOT NULL,
    display_order SMALLINT,
    parent_item_id UUID,
    is_default BOOLEAN NOT NULL,
    status VARCHAR(20) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT pk_catalog_item
        PRIMARY KEY (catalog_item_id),

    CONSTRAINT fk_catalog_item_catalog
        FOREIGN KEY (catalog_id)
        REFERENCES catalog.catalog (catalog_id),

    CONSTRAINT fk_catalog_item_parent
        FOREIGN KEY (parent_item_id)
        REFERENCES catalog.catalog_item (catalog_item_id)
);

CREATE INDEX idx_catalog_item
    ON catalog.catalog_item (catalog_id);

CREATE INDEX idx_item_code
    ON catalog.catalog_item (item_code);

CREATE INDEX idx_item_parent
    ON catalog.catalog_item (parent_item_id);