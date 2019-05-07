CREATE TABLE IF NOT EXISTS events (
    "event"              VARCHAR(23) NOT NULL,
    "timestamp"          TIMESTAMP   NOT NULL,
    "customer.id"        CHAR(36),
    "customer.is_vip"    BOOLEAN,
    "employee.id"        CHAR(36),
    "employee.job_role"  VARCHAR(12),
    "location.elevation" SMALLINT,
    "location.latitude"  DOUBLE PRECISION,
    "location.longitude" DOUBLE PRECISION,
    "package.id"         CHAR(36),
    "vehicle.mileage"    INT,
    "vehicle.vin"        CHAR(17)
);
