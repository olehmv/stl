package csv

import java.io.File

import csv.CrimeRecord._

import scala.io.Source

/**
  *
  * Utils to work with Crime Records Models
  */
object Utils {

  /** Collect data from files:
    * read files from folder,
    * read lines from file,
    * drop csv headers
    *
    * @param dir files location
    * @return List[List[String]]
    **/
  def readLines(dir: String): List[List[String]] = {
    val lines = new File(dir).listFiles.filter(_.getName.endsWith(".csv")).map(f => Source.fromFile(f).getLines().toList.drop(1)).toList
    lines.flatMap(l => l.map(s => s.split(",").toList))
  }

  /**
    * Compose Crime Records from csv lines
    *
    * @param lines csv
    * @return List[CrimeRecord]
    */
  def compose(lines: List[List[String]]): List[CrimeRecord] = {
    lines.map(l => build(l))
  }

  /**
    * Filter Crime Records with empty Crime Id
    *
    * @param list Crime Records
    * @return List[CrimeRecord]
    */
  def filterEmptyCrimeID(list: List[CrimeRecord]): List[CrimeRecord] = {
    list.filter(e => e.crimeID != "?")
  }

  /**
    * Group Crime Records by coordinates
    *
    * @param list Crime Records
    * @return List[CrimeRecord]
    */
  def groupByCoordinates(list: List[CrimeRecord]): List[CrimeRecord] = {
    list.groupBy(e => (e.longitude, e.latitude)).flatMap(t => t._2).toList
  }

  /**
    * Add total number of crime per coordinates to Crime Records model and sort by total number of crime per coordinates
    *
    * @param list Crime Records
    * @return List[CrimeRecord]
    */
  def sortByTotalNumberOfCrime(list: List[CrimeRecord]) = {
    list.filterNot(cr => cr.location == "No Location").groupBy(e => (e.longitude, e.latitude)).flatMap {
      t =>
        val count = t._2.size
        t._2.map(e => e.copy(13, count.toString))
    }.toList.sortWith((cr1, cr2) => cr1.locationCrimeNumber.toInt > cr2.locationCrimeNumber.toInt).take(5)
  }

  /**
    * Implicit Ordering by total crime number per coordinates
    */
  implicit object OrderByLocationCrimeNumber extends Ordering[((String, String), List[CrimeRecord])] {
    def compare(l1: ((String, String), List[CrimeRecord]), l2: ((String, String), List[CrimeRecord])): Int = {
      val l1Number = l1._2.size
      val l2Number = l2._2.size
      if (l1Number > l2Number) -1 else if (l1Number < l2Number) 1 else 0
    }

  }

  /**
    * Select top crime locations
    *
    * @param list Crime Records
    * @param top  top Crime Location
    * @return List[CrimeLocation]
    */
  def topCrimeLocations(list: List[CrimeRecord], top: Int): List[CrimeLocation] = {
    list.filterNot(cr => cr.location == "No Location")
      .groupBy(e => (e.longitude, e.latitude)).toList.sorted
      .flatMap(t => t._2.take(1).map(cr => CrimeLocation(cr.longitude, cr.latitude, cr.location, cr.crimetype, cr.locationCrimeNumber))).take(top)
  }

  /**
    * Select top crime locations with list of incidents per locations
    *
    * @param list Crime Records
    * @param top  top Crime Location
    * @return List[LocationTheftIncidents]
    */
  def topCrimeLocationsIncidents(list: List[CrimeRecord], top: Int): List[LocationTheftIncidents] = {
    list.filterNot(cr => cr.location == "No Location")
      .groupBy(e => (e.longitude, e.latitude)).toList.sorted
      .map(t => LocationTheftIncidents(t._1._1, t._1._2, t._2.size.toString, t._2.map(cr => cr.crimetype))).take(top)
  }


}

