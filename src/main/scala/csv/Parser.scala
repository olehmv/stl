package csv

import csv.Utils._
import org.slf4j.{Logger, LoggerFactory}

/**
  * Main class
  */
object Parser extends App {
  /**
    * Logger
    */
  def log: Logger = LoggerFactory.getLogger(this.getClass)

  /**
    * Files lines
    */
  val lines: List[List[String]] = readLines(args(0))
  log.info(s"${lines.size} Lines Read")
  /**
    * List of modeled crime records
    */
  val crimeRecords: List[CrimeRecord] = compose(lines)
  log.info(s"${crimeRecords.size} Crime Records Composed")
  /**
    * List of filtered by Not Empty Crime Id crime records
    */
  val filteredCrimeRecords: List[CrimeRecord] = filterEmptyCrimeID(crimeRecords)
  log.info(s"${crimeRecords.size - filteredCrimeRecords.size} Empty CrimeID Crime Records")
  log.info(s"${filteredCrimeRecords.size} Not Empty CrimeID Crime Records")
  log.info(s"Grouping Crime Records")
  /**
    * List of grouped by coordinates crime records
    */
  val groupedRecords: List[CrimeRecord] = groupByCoordinates(filteredCrimeRecords)
  log.info(s"${groupedRecords.take(5) mkString ("\n")}\n...Crime Records Grouped By Coordinates")
  log.info(s"Sorting Crime Records")
  /**
    * List of sorted by number of crime per coordinates crime records
    */
  val sortedrecords: List[CrimeRecord] = sortByTotalNumberOfCrime(filteredCrimeRecords)
  log.info(s"${sortedrecords.take(5).mkString("\n")}\n...Crime Records Sorted By Total Number Of Crime Per Location")
  log.info("Selecting Top 5 Crime Locations")
  /**
    * List of top 5 modeled crime locations
    */
  val top5Locations: List[CrimeLocation] = topCrimeLocations(filteredCrimeRecords, 5)
  log.info(s"${top5Locations.take(5).mkString("\n")}\n...Top 5 Crime Locations")
  log.info("Selecting Top 5 Crime Locations Incidents")
  /**
    * List of top 5 locations with list of incidents per location
    */
  val top5LocationsIncidents: List[LocationTheftIncidents] = topCrimeLocationsIncidents(filteredCrimeRecords, 5)
  log.info(s"${top5LocationsIncidents.take(5).mkString("\n")}\n...Top 5 Crime Locations Incidents")
}
