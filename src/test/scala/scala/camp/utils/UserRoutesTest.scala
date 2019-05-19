import akka.actor.ActorRef
import akka.http.scaladsl.marshalling.Marshal
import akka.http.scaladsl.model._
import akka.http.scaladsl.testkit.ScalatestRouteTest
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.{Matchers, WordSpec}

import scala.camp.model.User
import scala.camp.routes.UserRoutes

class UserRoutesTest extends WordSpec with Matchers with ScalaFutures with ScalatestRouteTest
  with UserRoutes {


  lazy val userRoutes = routes

  "UserRoutes" should {
    "be able to add users (POST /user)" in {
      val user = User(0, "Kolya", Some("Lviv"), "kolya@email.com")

      val userEntity = Marshal(user).to[MessageEntity].futureValue

      val request = Post("/user").withEntity(userEntity)

      request ~> routes ~> check {
        status should ===(StatusCodes.Created)

        contentType should ===(ContentTypes.`application/json`)

        entityAs[String] should ===("""{"id":1,"username":"Kolya","address":"Lviv","email":"kolya@email.com"}""")
      }
    }
    "return user by id (GET /user?id=1)" in{

      val request = HttpRequest(uri = "/user?id=1")

      request ~> routes ~> check {
        status should ===(StatusCodes.OK)
        contentType should ===(ContentTypes.`application/json`)
        entityAs[String] should ===("""{"id":1,"username":"Kolya","address":"Lviv","email":"kolya@email.com"}""")
      }

    }


  }

}