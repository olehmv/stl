package csv

import org.scalatest.{BeforeAndAfter, FlatSpec, Matchers}

class CrimeRecordTest extends FlatSpec with Matchers with BeforeAndAfter {

  var actual: List[String] = null
  var expected: CrimeRecord = null
  var crimeRecordBuilder: List[String] => CrimeRecord = null

  before {
    actual = List("67434aed9b78a65aa82c33e216ffe4694fb333948a98fa42e217613a99a40440", "2018-12",
      "Dorset Police", "Dorset Police", "-1.889120", "50.766371", "On or near Wimborne Road",
      "E01015301", "Bournemouth 001A", "Robbery", "Under investigation","context unknown")
    expected = new CrimeRecord("67434aed9b78a65aa82c33e216ffe4694fb333948a98fa42e217613a99a40440", "2018-12",
      "Dorset Police", "Dorset Police", "-1.889120", "50.766371", "On or near Wimborne Road",
      "E01015301", "Bournemouth 001A", "Robbery", "Under investigation","context unknown")
    crimeRecordBuilder = CrimeRecord.build _
  }
  "A CrimeRecord" should "build CrimeRecord from list of strings" in {
    crimeRecordBuilder(actual).toString should be (expected.toString)
  }

}
