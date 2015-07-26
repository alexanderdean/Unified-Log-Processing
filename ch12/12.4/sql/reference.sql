CREATE TABLE vehicles(
  vin CHAR(17) NOT NULL,
  make VARCHAR(32) NOT NULL,
  model VARCHAR(32) NOT NULL,
  year SMALLINT);
INSERT INTO vehicles VALUES
  ('1HGCM82633A004352', 'Ford', 'Transit', 2005),
  ('JH4TB2H26CC000000', 'VW', 'Caddy', 2010),
  ('19UYA31581L000000', 'GMC', 'Savana', 2011);

CREATE TABLE employees(
  id CHAR(36) NOT NULL,
  name VARCHAR(32) NOT NULL,
  dob DATE NOT NULL);
INSERT INTO employees VALUES
  ('f2caa6a0-2ce8-49d6-b793-b987f13cfad9', 'Amanda', '1992-01-08'),
  ('f6381390-32be-44d5-9f9b-e05ba810c1b7', 'Rohan', '1983-05-17'),
  ('3b99f162-6a36-49a4-ba2a-375e8a170928', 'Louise', '1978-11-25'),
  ('54997a47-252d-499f-a54e-1522ac49fa48', 'Carlos', '1985-10-27'),
  ('c4b843f2-0ef6-4666-8f8d-91ac2e366571', 'Andreas', '1994-03-13');

CREATE TABLE packages(
  id CHAR(36) NOT NULL,
  weight INT NOT NULL);
INSERT INTO packages VALUES
  ('c09e4ee4-52a7-4cdb-bfbf-6025b60a9144', 564),
  ('ec99793d-94e7-455f-8787-1f8ebd76ef61', 1300),
  ('14a714cf-5a89-417e-9c00-f2dba0d1844d', 894),
  ('834bc3e0-595f-4a6f-a827-5580f3d346f7', 3200),
  ('79fee326-aaeb-4cc6-aa4f-f2f98f443271', 2367);

CREATE TABLE customers(
  id CHAR(36) NOT NULL,
  name VARCHAR(32) NOT NULL,
  zip_code VARCHAR(10) NOT NULL);
INSERT INTO customers VALUES
  ('b39a2b30-049b-436a-a45d-46d290df65d3', 'Karl', '99501'),
  ('4594f1a1-a7a2-4718-bfca-6e51e73cc3e7', 'Maria', '72217-2517'),
  ('b1e5d874-963b-4992-a232-4679438261ab', 'Amit', '90089');
