CREATE TABLE employees
(
    id                  INTEGER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    tenant_id           INTEGER       NOT NULL,
    employee_number     VARCHAR(50),
    name                VARCHAR(255)  NOT NULL,
    email               VARCHAR(255),
    personal_phone      VARCHAR(50),
    work_phone          VARCHAR(50),
    address_id          INTEGER,
    date_of_birth       DATE,
    created_at          TIMESTAMP     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at          TIMESTAMP     NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk__employees__tenant FOREIGN KEY (tenant_id) REFERENCES tenants (id) ON DELETE CASCADE,
    CONSTRAINT fk__employees__address FOREIGN KEY (address_id) REFERENCES addresses (id) ON DELETE SET NULL
);

CREATE INDEX idx__employees__tenant_id ON employees (tenant_id);
CREATE INDEX idx__employees__employee_number ON employees (employee_number);
CREATE INDEX idx__employees__email ON employees (email); 