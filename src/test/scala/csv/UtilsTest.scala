package csv

import org.scalatest.{BeforeAndAfter, FlatSpec, Matchers}


class UtilsTest extends FlatSpec with Matchers with BeforeAndAfter {

  var lines: List[List[String]] = null
  var crimeRecords:List[CrimeRecord]=null
  var filteredCrimeRecords:List[CrimeRecord]=null
  var groupedCrimeRecords:List[CrimeRecord]=null
  var sortedCrimeRecords:List[CrimeRecord]=null
  var topCrimeLocations:List[CrimeLocation]=null
  var topCrimeLocationTheftIncidents:List[LocationTheftIncidents]=null
  before {
    lines = Utils.readLines("src/test/resources/crimes")
    crimeRecords=Utils.compose(lines)
    filteredCrimeRecords=Utils.filterEmptyCrimeID(crimeRecords)
    groupedCrimeRecords = Utils.groupByCoordinates(crimeRecords)
    sortedCrimeRecords = Utils.sortByTotalNumberOfCrime(filteredCrimeRecords)
    topCrimeLocations=Utils.topCrimeLocations(filteredCrimeRecords,2)
    topCrimeLocationTheftIncidents = Utils.topCrimeLocationsIncidents(filteredCrimeRecords,2)
  }


  "A Utils" should "read lines from csv files in folder with out headers" in {
    lines.size should be(9)
  }

  it should "composed crime records" in {
    crimeRecords.size should be(9)
  }
  it should "filter empty crimeID crime records" in {
    filteredCrimeRecords.size should be (6)
  }
  it should "group crime records by coordinates" in {
    groupedCrimeRecords
      .filter(cr=>cr.longitude=="-1.889120" && cr.latitude=="50.766371" || cr.longitude=="1.889120" && cr.latitude=="50.766371")
      .size should be (5)
  }
  it should "sort crime records by total number of crime per location" in {
    sortedCrimeRecords
      .map(cr=>cr.locationCrimeNumber) should be (List("3", "3", "3", "2", "2"))
  }
  it should "find top crime Locations" in{
    topCrimeLocations
      .map(cl=>cl.locationCrimeNumber) should be (List("3","2"))
  }
  it should "find top crime locations incidents" in{
    topCrimeLocationTheftIncidents
      .map(lis=>(lis.locationCrimeNumber,lis.crimetypes).toString())should be (List((3,List("Other theft", "Robbery", "Vehicle crime")).toString(),(2,List("Bicycle theft", "?")).toString()))
  }

}