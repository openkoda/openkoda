--creates default organization
INSERT INTO organization (id, name) values (121, 'ACME');

-- create file_reference table
-- It is the @ElementCollection table and is not created automatically when spring.jpa.hibernate.ddl-auto=create
DROP TABLE IF EXISTS file_reference;
CREATE TABLE IF NOT EXISTS file_reference (
    file_id bigint references file(id),
    organization_related_entity_id bigint NOT NULL,
    sequence integer NOT NULL,
    field varchar NOT NULL default '',
    PRIMARY KEY (organization_related_entity_id, sequence, field)
)