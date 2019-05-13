package scala.camp.routes

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import akka.http.scaladsl.model.StatusCodes.NotFound
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import spray.json.DefaultJsonProtocol

import scala.camp.model.{JsonSupport, User}
import scala.camp.repository.UserRepository
import scala.concurrent.ExecutionContext


class UserRoutes(implicit ex: ExecutionContext)
    extends UserRepository with JsonSupport {

  val routes: Route = pathPrefix("user") {
    pathEndOrSingleSlash {
      post {
        entity(as[User]) { user =>
          onSuccess(registerUser(user)) { i =>
            complete(user.copy(id = i))
          }
        }
      }
      get {
        path(IntNumber) { id =>
          onSuccess(getById(id)) { user =>
            user match {
              case Some(x) => complete(x)
              case None => complete(NotFound)
            }
          }
        }
      }
    }
  }
}
