package scala.camp.routes
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import akka.http.scaladsl.server.directives.Credentials

import scala.camp.model.{BasicAuthCredentials, JsonSupport, LoggedInUser}
import scala.camp.proxy.AuthServer.{authenticateBasic, authenticateOAuth2, complete, get, path, pathEndOrSingleSlash, post}
import scala.camp.repository.AuthRepository
import scala.collection.mutable
import scala.language.postfixOps
trait AuthRoutes extends JsonSupport with AuthRepository{

  import de.heikoseeberger.akkahttpjson4s.Json4sSupport._

  import scala.concurrent.ExecutionContext.Implicits.global

  private def BasicAuthAuthenticator(credentials: Credentials): Option[BasicAuthCredentials] =
    credentials match {
      case p @ Credentials.Provided(_) =>
        findBasicAuthCredentials(p.identifier.toInt).map{
          optionCred=>
          optionCred.getOrElse(None)
        }
        validBasicAuthCredentials.find(user =>
          user.username == p.identifier && p.verify(user.password))
      case _ => None
    }

  private def oAuthAuthenticator(credentials: Credentials): Option[LoggedInUser] =
    credentials match {
      case p @ Credentials.Provided(_) =>
//        loggedInUsers.find(user => p.verify(user.oAuthToken.access_token))
      case _ => None
    }

  // TODO load from external source
  private val validBasicAuthCredentials = Seq(BasicAuthCredentials("oleh", "oleh"))

  // TODO persist to make sessions survive restarts
  private val loggedInUsers = mutable.ArrayBuffer.empty[LoggedInUser]

  def authRoutes: Route =
    pathEndOrSingleSlash {
      get {
        complete("Welcome!")
      }
    } ~
      path("auth") {
        authenticateBasic(realm = "auth", BasicAuthAuthenticator) { user =>
          post {
            val loggedInUser = LoggedInUser(???)
            loggedInUsers.append(loggedInUser)
            complete(loggedInUser.oAuthToken)
          }
        }
      } ~
      path("api") {
        authenticateOAuth2(realm = "api", oAuthAuthenticator) { validToken =>
          complete(s"It worked! user = $validToken")
        }
      }

}
