package aowlambda

import org.joda.time.DateTime, aowlambda.{TruckArrives => TA},
  aowlambda.{TruckDeparts => TD}, aowlambda.{MechanicChangesOil => MCO}

case class Row(vin: String, mileage: Int, mileageAtOilChange: Option[Int],
  locationTs: Option[(Location, DateTime)])                        // a

object Aggregator {

  def map(event: Event): Option[Row] = event match {               // b
    case TA(ts, v, loc) => Some(Row(v.vin, v.mileage, None, Some(loc, ts)))
    case TD(ts, v, loc) => Some(Row(v.vin, v.mileage, None, Some(loc, ts)))
    case MCO(ts, _, v)  => Some(Row(v.vin, v.mileage,Some(v.mileage),None))
    case _              => None
  }

  def reduce(events: List[Option[Row]]): List[Row] =               // c
    events
      .collect { case Some(r) => r }
      .groupBy(_.vin)
      .values
      .toList
      .map(_.reduceLeft(merge))

  private val merge: (Row, Row) => Row = (a, b) => {               // d

    val m = math.max(a.mileage, b.mileage)
    val maoc = (a.mileageAtOilChange, b.mileageAtOilChange) match {
      case (l @ Some(_), None) => l
      case (l @ Some(lMaoc), Some(rMaoc)) if lMaoc > rMaoc => l
      case (_, r) => r
    }
    val locTs = (a.locationTs, b.locationTs) match {
      case (l @ Some(_), None) => l
      case (l @ Some((_, lTs)), Some((_, rTs))) if lTs.isAfter(rTs) => l
      case (_, r) => r
    }
    Row(a.vin, m, maoc, locTs)
  }
}
