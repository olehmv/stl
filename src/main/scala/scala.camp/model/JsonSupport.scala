package scala.camp.model

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import org.json4s.native.Serialization
import org.json4s.{DefaultFormats, native}
import spray.json.DefaultJsonProtocol

trait JsonSupport extends SprayJsonSupport with DefaultJsonProtocol {
  implicit val td = jsonFormat4(User.apply)
  implicit val userAuth = jsonFormat5(UserAuth.apply)
  implicit val formats: DefaultFormats.type = DefaultFormats
  implicit val serialization: Serialization.type = native.Serialization
}
