#!/usr/bin/env python

import random
import datetime
import json
import copy

class Jsonable:
  def to_json(self):
    return json.dumps(self, default=lambda o: o.__dict__, 
        sort_keys=True, separators=(',',':'))

class Employee(Jsonable):
  def __init__(self, id, job_role):
    self.id = id
    self.job_role = job_role

  def promote(self):
    if self.job_role == "JNR_MECHANIC":
      self.job_role = "SNR_MECHANIC"
    elif self.job_role == "JNR_DRIVER":
      self.job_role = "SNR_DRIVER"

class Vehicle(Jsonable):
  def __init__(self, vin, mileage):
    self.vin = vin
    self.mileage = mileage

  def add_miles(self):
    self.mileage += random.randint(0,30)
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
    self.is_vip = is_vip

class Event(object, Jsonable):
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

  def advance(self):
    self.timestamp += datetime.timedelta(0, 0, 0, 0, random.randint(0,300))
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

DEPOT_LOC = Location(51.5228340, -0.0818130, 7)
GARAGE_LOC = Location(51.4865047, -0.0639602, 4)
CUSTOMERS_LOCS_UNRELIABLE = [
  # Tuple3 of customer, location, and whether they are unreliable (i.e. often out)
  (Customer("b39a2b30-049b-436a-a45d-46d290df65d3", True), Location(51.5208046, -0.1592323, -25), False),
  (Customer("4594f1a1-a7a2-4718-bfca-6e51e73cc3e7", False), Location(51.4972997, -0.0955459, 102), True),
  (Customer("b1e5d874-963b-4992-a232-4679438261ab", False), Location(51.4704679, -0.1176902, 15), False)
]

packages = set([
  (Package("c09e4ee4-52a7-4cdb-bfbf-6025b60a9144"), CUSTOMERS_LOCS_UNRELIABLE[0]),
  (Package("ec99793d-94e7-455f-8787-1f8ebd76ef61"), CUSTOMERS_LOCS_UNRELIABLE[1]),
  (Package("14a714cf-5a89-417e-9c00-f2dba0d1844d"), CUSTOMERS_LOCS_UNRELIABLE[1]),
  (Package("834bc3e0-595f-4a6f-a827-5580f3d346f7"), CUSTOMERS_LOCS_UNRELIABLE[2]),
  (Package("79fee326-aaeb-4cc6-aa4f-f2f98f443271"), CUSTOMERS_LOCS_UNRELIABLE[0]) 
])

START_OF_YEAR = datetime.datetime(2015, 1, 1)

if __name__ == '__main__':

  events = []
  clock = Clock(START_OF_YEAR)

  # 20 oil change trips
  for x in range(0, 19):
    # Choose truck
    truck = random.choice(TRUCKS)
    # Choose mechanic with weight
    if random.randint(0,8) >= 6:
      mechanic = MECHANICS[0]
    else:
      mechanic = MECHANICS[1]

    events += [
      TruckDepartsEvent(clock.advance(), truck.add_miles(), DEPOT_LOC),
      TruckArrivesEvent(clock.advance(), truck.add_miles(), GARAGE_LOC),
      MechanicChangesOil(clock.advance(), mechanic, truck),
      TruckDepartsEvent(clock.advance(), truck.add_miles(), GARAGE_LOC),
      TruckArrivesEvent(clock.advance(), truck.add_miles(), DEPOT_LOC)
    ]

    # Junior mechanic gets a promotion halfway through
    if x == 9:
      MECHANICS[1].promote()

  # Delivery trips till all packages delivered
  while packages:
    # Choose package and thus customer
    package_customer = random.sample(packages, 1)
    package = package_customer[0][0]
    customer_details = package_customer[0][1]

    # Choose driver
    driver = random.choice(DRIVERS)

    # Is customer out with weight
    if customer_details[2] == True:
      weight = 2
    else:
      weight = 7

    customer = customer_details[0]
    customer_loc = customer_details[1]
    customer_out = (random.randint(0,8) >= weight)

    events += [
      TruckDepartsEvent(clock.advance(), truck.add_miles(), DEPOT_LOC),
      TruckArrivesEvent(clock.advance(), truck.add_miles(), customer_loc),
      DriverMissesCustomer(clock.advance(), driver, package, customer, customer_loc) if customer_out else DriverDeliversPackage(clock.advance(), driver, package, customer, customer_loc),
      TruckDepartsEvent(clock.advance(), truck.add_miles(), customer_loc),
      TruckArrivesEvent(clock.advance(), truck.add_miles(), DEPOT_LOC)
    ]

    if not customer_out:
      packages -= set(package_customer)

  # Now write out
  with open('events.ndjson', 'w') as f:
    for e in events:
      f.write(e.to_json() + "\n")
