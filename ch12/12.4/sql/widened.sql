CREATE VIEW widened AS
  SELECT
    ev.event         AS event,
    ev.timestamp     AS timestamp,
    "ev.vehicle.vin" AS "vehicle.vin",
    "v.make"         AS "vehicle.make",
    "v.model"        AS "vehicle.model",
    "v.year"         AS "vehicle.year",
    "ev.location.longitude" AS "location.longitude",
    "ev.location.latitude"  AS "location.latitude",
    "ev.location.elevation" AS "location.elevation",
    "ev.employee.id" AS "employee.id",
    "e.name"         AS "employee.name",
    "e.dob"          AS "employee.dob",
    "ev.package.id"  AS "package.id",
    "p.weight"       AS "package.weight",
    "ev.customer.id" AS "customer.id",
    "c.name"         AS "customer.name",
    "c.zip"          AS "customer.zip"
  FROM events ev
    LEFT JOIN vehicles v  ON ev.vehicle.vin = v.vin
    LEFT JOIN employees e ON ev.employee.id = e.id
    LEFT JOIN packages p  ON ev.package.id  = p.id
    LEFT JOIN customers c ON ev.customer.id = c.id;
