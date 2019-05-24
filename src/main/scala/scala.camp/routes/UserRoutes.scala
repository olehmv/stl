package scala.camp.routes

import java.time.LocalDateTime

import akka.http.scaladsl.model.StatusCodes.NotFound
import akka.http.scaladsl.model.{HttpResponse, StatusCodes}
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server._
import akka.http.scaladsl.server.directives.Credentials

import scala.camp.model.{JsonSupport, User, UserAuth}
import scala.camp.repository.{UserRepository}
import scala.camp.utils.{Retry, Validator}
import scala.concurrent.Future
import scala.concurrent.duration._
import scala.util.Try
import scala.concurrent._
import ExecutionContext.Implicits.global

trait UserRoutes extends UserRepository with JsonSupport with Retry {

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

  private def BasicAuthAuthenticator(credentials: Credentials): Option[User] =
    credentials match {
      case p @ Credentials.Provided(_) =>
        val f: Future[Option[User]] = getByName(p.identifier)
        val maybeUser = Await.result(f, 2.seconds)
        maybeUser
      case _ => None
    }

  private def oAuthAuthenticator(credentials: Credentials): Option[UserAuth] =
    credentials match {
      case p @ Credentials.Provided(_) =>
        Await.result(findUser(p.identifier), 2.seconds) match {
          case Some(user) =>
            if (p.verify(user.accessToken)) {
              Some(user)
            } else {
              None
            }
          case None => None
        }
      case _ => None
    }

  val userRoutes: Route = pathPrefix("user") {
    path("userAuth") {
      get {
        parameters('name) { name =>
          onSuccess(findUser(name)) { user =>
            user match {
              case Some(x) => complete(x)
              case None    => complete(NotFound)
            }
          }
        }
      }

    } ~
      path("auth") {
        authenticateBasic(realm = "auth", BasicAuthAuthenticator) { user: User =>
          post {
            val authUser = UserAuth(accessToken = java.util.UUID.randomUUID().toString,
                                    tokenType = "bearer",
                                    expiresIn = 3600,
                                    loggedInAt = LocalDateTime.now().toString)
            onSuccess(authenticateUser(authUser)) { i =>
              complete(authUser.accessToken)
            }
          }
        }
      } ~
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
          authenticateOAuth2(realm = "user", oAuthAuthenticator) { authUser =>
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

}
