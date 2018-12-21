#!/usr/bin/env python

import random, datetime, json, copy, time, sys, boto3

class Jsonable:
  def to_json(self):
    return json.dumps(self, default=lambda o: o.__dict__, 
        sort_keys=True, separators=(',',':'))

class Employee(Jsonable):
  def __init__(self, id, job_role):
    self.id = id
    self.jobRole = job_role

  def promote(self):
    if self.jobRole == "JNR_MECHANIC":
      self.jobRole = "SNR_MECHANIC"
    elif self.jobRole == "JNR_DRIVER":
      self.jobRole = "SNR_DRIVER"

class Vehicle(Jsonable):
  def __init__(self, vin, mileage):
    self.vin = vin
    self.mileage = mileage

  def step_miles(self, clock_direction):
    self.mileage += (clock_direction * random.randint(0,30))
    return self

class Location(Jsonable):
  def __init__(self, latitude, longitude, elevation):
    self.longitude = longitude
    self.latitude = latitude
    self.elevation = elevation

class Package(Jsonable):
  def __init__(self, id):
    self.id = id

class Customer(Jsonable):
  def __init__(self, id, is_vip):
    self.id = id
    self.isVip = is_vip

class Event(Jsonable):
  def __init__(self, event, timestamp):
    self.event = event
    self.timestamp = timestamp.isoformat() + "Z"

class TruckArrivesEvent(Event):
  def __init__(self, timestamp, vehicle, location):
    self.vehicle = copy.copy(vehicle)
    self.location = location
    super(TruckArrivesEvent, self).__init__("TRUCK_ARRIVES", timestamp)

class TruckDepartsEvent(Event):
  def __init__(self, timestamp, vehicle, location):
    self.vehicle = copy.copy(vehicle)
    self.location = location
    super(TruckDepartsEvent, self).__init__("TRUCK_DEPARTS", timestamp)

class MechanicChangesOil(Event):
  def __init__(self, timestamp, employee, vehicle):
    self.employee = copy.copy(employee)
    self.vehicle = copy.copy(vehicle)
    super(MechanicChangesOil, self).__init__("MECHANIC_CHANGES_OIL", timestamp)

class DriverDeliversPackage(Event):
  def __init__(self, timestamp, employee, package, customer, location):
    self.employee = copy.copy(employee)
    self.package = package
    self.customer = copy.copy(customer)
    self.location = location
    super(DriverDeliversPackage, self).__init__("DRIVER_DELIVERS_PACKAGE", timestamp)

class DriverMissesCustomer(Event):
  def __init__(self, timestamp, employee, package, customer, location):
    self.employee = employee
    self.package = package
    self.customer = customer
    self.location = location
    super(DriverMissesCustomer, self).__init__("DRIVER_MISSES_CUSTOMER", timestamp)

class Clock:
  def __init__(self, timestamp):
    self.timestamp = timestamp

  def step(self, clock_direction):
    self.timestamp += datetime.timedelta(0, 0, 0, 0, (clock_direction * random.randint(0,300)))
    return self.timestamp

MECHANICS = [
  Employee("f2caa6a0-2ce8-49d6-b793-b987f13cfad9", "SNR_MECHANIC"),
  Employee("f6381390-32be-44d5-9f9b-e05ba810c1b7", "JNR_MECHANIC")
]

DRIVERS = [
  Employee("3b99f162-6a36-49a4-ba2a-375e8a170928", "SNR_DRIVER"),
  Employee("54997a47-252d-499f-a54e-1522ac49fa48", "JNR_DRIVER"),
  Employee("c4b843f2-0ef6-4666-8f8d-91ac2e366571", "JNR_DRIVER")
]

TRUCKS = [
  Vehicle("1HGCM82633A004352", 32332),
  Vehicle("JH4TB2H26CC000000", 7839),
  Vehicle("19UYA31581L000000", 6754)
]

LOCATIONS = [
  Location(51.5228340, -0.0818130, 7),
  Location(51.5208046, -0.1592323, -25),
  Location(51.4972997, -0.0955459, 102),
  Location(51.4704679, -0.1176902, 15),
  Location(51.4865047, -0.0639602, 4)
]

CUSTOMERS = [
  Customer("b39a2b30-049b-436a-a45d-46d290df65d3", True),
  Customer("4594f1a1-a7a2-4718-bfca-6e51e73cc3e7", False),
  Customer("b1e5d874-963b-4992-a232-4679438261ab", False)
]

PACKAGES = [
  Package("c09e4ee4-52a7-4cdb-bfbf-6025b60a9144"),
  Package("ec99793d-94e7-455f-8787-1f8ebd76ef61"),
  Package("14a714cf-5a89-417e-9c00-f2dba0d1844d"),
  Package("834bc3e0-595f-4a6f-a827-5580f3d346f7"),
  Package("79fee326-aaeb-4cc6-aa4f-f2f98f443271")
]

clock = Clock(datetime.datetime(2018, 1, 1))

def write_event(conn, stream_name, clock_direction):
  global clock

  mechanic = random.choice(MECHANICS)
  driver = random.choice(DRIVERS)
  truck = random.choice(TRUCKS).step_miles(clock_direction)
  location = random.choice(LOCATIONS)
  customer = random.choice(CUSTOMERS)
  package = random.choice(PACKAGES)
  timestamp = clock.step(clock_direction)

  event = random.choice([
    TruckDepartsEvent(timestamp, truck, location),
    TruckArrivesEvent(timestamp, truck, location),
    MechanicChangesOil(timestamp, mechanic, truck),
    DriverMissesCustomer(timestamp, driver, package, customer, location),
    DriverDeliversPackage(timestamp, driver, package, customer, location)
    ])

  conn.put_record(StreamName=stream_name, Data=event.to_json(), PartitionKey=str(random.random()))
  return (event.__class__.__name__, timestamp)

if __name__ == '__main__':
  session = boto3.Session(profile_name="ulp")
  conn = session.client("kinesis", region_name="us-east-1")

  if len(sys.argv) >= 2 and sys.argv[1] == "backwards":
    step_direction = -1
  else:
    step_direction = 1

  while True:
    event, ts = write_event(conn, "oops-events", step_direction)
    print(f'Wrote {event} with timestamp {ts}')
    time.sleep(1)
