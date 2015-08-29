package aowlambda

import java.util.UUID, org.joda.time.DateTime
import org.json4s._, org.json4s.jackson.JsonMethods._

case class EventSniffer(event: String)                              // a
case class Employee(id: UUID, jobRole: String)
case class Vehicle(vin: String, mileage: Int)
case class Location(latitude: Double, longitude: Double, elevation: Int)
case class Package(id: UUID)
case class Customer(id: UUID, isVip: Boolean)

sealed trait Event                                                  // b
case class TruckArrives(timestamp: DateTime, vehicle: Vehicle, 
  location: Location) extends Event
case class TruckDeparts(timestamp: DateTime, vehicle: Vehicle, 
  location: Location) extends Event
case class MechanicChangesOil(timestamp: DateTime, employee: Employee, 
  vehicle: Vehicle) extends Event
case class DriverDeliversPackage(timestamp: DateTime, employee: Employee,   
  `package`: Package, customer: Customer, location: Location) extends Event
case class DriverMissesCustomer(timestamp: DateTime, employee: Employee,   
  `package`: Package, customer: Customer, location: Location) extends Event

object Event {

  def fromBytes(byteArray: Array[Byte]): Event = {
    implicit val formats = DefaultFormats ++ ext.JodaTimeSerializers.all
    val raw = parse(new String(byteArray, "UTF-8"))
    raw.extract[EventSniffer].event match {                        // c
      case "TRUCK_ARRIVES" => raw.extract[TruckArrives]
      case "TRUCK_DEPARTS" => raw.extract[TruckDeparts]
      case "MECHANIC_CHANGES_OIL" => raw.extract[MechanicChangesOil]
      case "DRIVER_DELIVERS_PACKAGE" => raw.extract[DriverDeliversPackage]
      case "DRIVER_MISSES_CUSTOMER" => raw.extract[DriverMissesCustomer]
      case e => throw new RuntimeException("Didn't expect " + e)   // d
    }
  }
}
