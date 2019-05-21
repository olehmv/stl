package scala.camp.routes

import akka.http.scaladsl.model.StatusCodes.NotFound
import akka.http.scaladsl.model.{HttpResponse, StatusCodes}
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server._

import scala.camp.model.{JsonSupport, User}
import scala.camp.repository.UserRepositoryImpl
import scala.camp.utils.{Retry, Validator}
import scala.concurrent.Future
import scala.concurrent.duration._

trait UserRoutes extends UserRepositoryImpl with JsonSupport with Retry {

  private val emailRegex =
    """^[a-zA-Z0-9\.!#$%&'*+/=?^_`{|}~-]+@[a-zA-Z0-9](?:[a-zA-Z0-9-]{0,61}[a-zA-Z0-9])?(?:\.[a-zA-Z0-9](?:[a-zA-Z0-9-]{0,61}[a-zA-Z0-9])?)*$""".r

  def checkEmail(e: String): Boolean = e match {
    case null                                          => false
    case e if e.trim.isEmpty                           => false
    case e if emailRegex.findFirstMatchIn(e).isDefined => true
    case _                                             => false
  }

  def emailValidator = new Validator[String] {
    override def validate(value: String): Either[String, String] =
      if (checkEmail(value)) Right(value) else Left(s"Email '$value' is Not valid!")
  }

  def retryUserPost(user: User): Future[Int] =
    retryf[Int](() => registerUser(user), id => id != 0, List(1.seconds, 2.seconds, 3.seconds))

  def retryUserGet(id: Int): Future[Option[User]] =
    retryf[Option[User]](() => getById(id),
                         user => user.isDefined,
                         List(1.seconds, 2.seconds, 3.seconds))

  implicit def myExceptionHandler = ExceptionHandler {
    case e: Throwable => {
      println(e.getMessage)
      println(e.getStackTrace)
      complete(HttpResponse(StatusCodes.BadRequest, entity = e.getMessage))
    }
  }

  val routes: Route = pathPrefix("user") {
    post {
      entity(as[User]) { user =>
        emailValidator.validate(user.email) match {
          case Right(v) =>
            onSuccess(retryUserPost(user)) { i =>
              complete(StatusCodes.Created, user.copy(id = i))
            }
          case Left(v) => complete(StatusCodes.BadRequest, v)
        }
      }
    } ~
      get {
        parameters('id) { id =>
          onSuccess(retryUserGet(id.toInt)) { user =>
            user match {
              case Some(x) => complete(x)
              case None    => complete(NotFound)
            }
          }
        }
      }
  }

}
