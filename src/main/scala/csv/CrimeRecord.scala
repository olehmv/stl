package csv

/**
  * Model for locations incidents crime types
  * @param longitude coordinates
  * @param latitude coordinates
  * @param locationCrimeNumber number of crime in location
  * @param crimetypes types of crime in location
  */
case class LocationTheftIncidents(longitude: String, latitude: String, locationCrimeNumber:String, crimetypes:List[String])

/**
  * Model for crime location
  * @param longitude coordinates
  * @param latitude coordinates
  * @param location location
  * @param crimetype type of crime in location
  * @param locationCrimeNumber number of crime in location
  */
case class CrimeLocation(longitude: String, latitude: String, location: String, crimetype:String, locationCrimeNumber:String)

/**
  * Model for crime record.
  * default value - ?
  * @param crimeID ?
  * @param month ?
  * @param reportedby ?
  * @param fallswithin ?
  * @param longitude ?
  * @param latitude ?
  * @param location ?
  * @param lSOAcode ?
  * @param lSOAname ?
  * @param crimetype ?
  * @param lastoutcomecategory ?
  * @param context ?
  * @param locationCrimeNumber ?
  */
class CrimeRecord(var crimeID: String = "?", var month: String = "?",
                  var reportedby: String = "?", var fallswithin: String = "?",
                  var longitude: String = "?", var latitude: String = "?",
                  var location: String = "?", var lSOAcode: String = "?",
                  var lSOAname: String = "?", var crimetype: String = "?",
                  var lastoutcomecategory: String = "?", var context: String = "?",
                  var locationCrimeNumber: String = "?") {


  /**
    * Copy element into model
    * @param position value position in model
    * @param value value to insert into model position
    * @return CrimeRecord
    */
  def copy(position: Int, value: String): CrimeRecord = {
    var string = value.trim
    if (string.isEmpty) string = "?"
    position match {
      case 13=>locationCrimeNumber=string
      case 12 => crimeID = string
      case 11 => month = string
      case 10 => reportedby = string
      case 9 => fallswithin = string
      case 8 => longitude = string
      case 7 => latitude = string
      case 6 => location = string
      case 5 => lSOAcode = string
      case 4 => lSOAname = string
      case 3 => crimetype = string
      case 2 => lastoutcomecategory = string
      case 1 => context = string
    }
    this
  }

  /**
    * Model view in csv
    * @return String
    */
  override def toString: String = s"${
    crimeID
  },${
    month
  },${
    reportedby
  },${
    fallswithin
  },${
    longitude
  },${
    latitude
  },${
    location
  },${
    lSOAcode
  },${
    lSOAname
  },${
    crimetype
  },${
    lastoutcomecategory
  },${
    context
  },${locationCrimeNumber}"
}

/**
  * Record Builder
  */
object CrimeRecord {

  /**
    * Crime Record model builder
    * @param list values to insert into model
    * @return CrimeRecord
    */
  def build(list: List[String]): CrimeRecord = {
    def go(i: Int, l: List[String]): CrimeRecord = {
      l match {
        case Nil => new CrimeRecord()
        case head :: tail => go(i - 1, tail).copy(i, head)
      }
    }
    go(12, list)
  }

}
