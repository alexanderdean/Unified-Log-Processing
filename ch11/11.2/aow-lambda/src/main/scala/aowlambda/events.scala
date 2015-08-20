package aowlambda

import java.util.UUID, org.joda.time.DateTime
import org.json4s._, org.json4s.jackson.JsonMethods._

case class EventSniffer(`type`: String)                             // a
case class Employee(id: UUID, jobRole: String)
case class Vehicle(vin: String, mileage: Int)
case class Location(latitude: Double, longitude: Double, elevation: Int)
case class Package(id: UUID)
case class Customer(id: UUID, isVip: Boolean)

sealed trait Event                                                  // b
case class TruckArrivesEvent(timestamp: DateTime, vehicle: Vehicle, 
  location: Location) extends Event
case class TruckDepartsEvent(timestamp: DateTime, vehicle: Vehicle, 
  location: Location) extends Event
case class MechanicChangesOil(timestamp: DateTime, employee: Employee, 
  vehicle: Vehicle) extends Event
case class DriverDeliversPackage(timestamp: DateTime, employee: Employee,   
  `package`: Package, customer: Customer, location: Location) extends Event
case class DriverMissesCustomer(timestamp: DateTime, employee: Employee,   
  `package`: Package, customer: Customer, location: Location) extends Event

object Event {

  def fromBytes(byteArray: Array[Byte]): Event = {
    implicit val formats = DefaultFormats
    val parsed = parse(new String(byteArray, "UTF-8"))
    parsed.extract[EventSniffer].`type` match {                    // c
      case "TRUCK_ARRIVES" => parsed.extract[TruckArrivesEvent]
      case "TRUCK_DEPARTS" => parsed.extract[TruckDepartsEvent]
      case "MECHANIC_CHANGES_OIL" => parsed.extract[MechanicChangesOil]
      case "DRIVER_DELIVERS_PACKAGE" => parsed.extract[DriverDeliversPackage]
      case "DRIVER_MISSES_CUSTOMER" => parsed.extract[DriverMissesCustomer]
      case _ => parsed.extract[Event]                              // d
    }
  }
}
