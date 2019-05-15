package scala.camp.routes

import akka.http.scaladsl.model.StatusCodes.NotFound
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server._

import scala.camp.model.{JsonSupport, User}
import scala.camp.repository.UserRepository
import akka.http.scaladsl.model.StatusCodes


trait UserRoutes
  extends UserRepository with JsonSupport {

  val routes: Route = pathPrefix("user") {
    post {
      entity(as[User]) { user =>
        onSuccess(registerUser(user)) { i =>
          complete(StatusCodes.Created,user.copy(id = i))
        }
      }
    }~
    get {
      parameters('id) {id =>
        onSuccess(getById(id.toInt)) { user =>
          user match {
            case Some(x) => complete(x)
            case None => complete(NotFound)
          }
        }
      }
    }
  }

}
