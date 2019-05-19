package scala.camp.proxy

import java.time.LocalDateTime

import akka.http.scaladsl.Http
import akka.http.scaladsl.server._
import akka.http.scaladsl.server.directives.Credentials
import com.typesafe.scalalogging.StrictLogging
import org.json4s.native.Serialization
import org.json4s.{DefaultFormats, native}

import scala.collection.mutable
import scala.concurrent.duration._
import scala.language.postfixOps

object Main  {
  def main(args: Array[String]): Unit = {
    val port: Int = sys.env.getOrElse("PORT", "8080").toInt
    AuthServer.startServer("0.0.0.0", port)
  }
}

object AuthServer extends HttpApp with StrictLogging {

  import de.heikoseeberger.akkahttpjson4s.Json4sSupport._

  implicit val formats: DefaultFormats.type = DefaultFormats
  implicit val serialization: Serialization.type = native.Serialization

  // TODO load from external source
  private val validBasicAuthCredentials = Seq(BasicAuthCredentials("oleh", "oleh"))

  // TODO persist to make sessions survive restarts
  private val loggedInUsers = mutable.ArrayBuffer.empty[LoggedInUser]

  override def postHttpBinding(binding: Http.ServerBinding): Unit = {
    systemReference.get().scheduler.schedule(5 minutes, 5 minutes)(cleanUpExpiredUsers())(systemReference.get().dispatcher)
    super.postHttpBinding(binding)
  }

  override protected def routes: Route =
    pathEndOrSingleSlash {
      get {
        complete("Welcome!")
      }
    } ~
      path("auth") {
        authenticateBasic(realm = "auth", BasicAuthAuthenticator) { user =>
          post {
            val loggedInUser = LoggedInUser(user)
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

  private def BasicAuthAuthenticator(credentials: Credentials): Option[BasicAuthCredentials] =
    credentials match {
      case p @ Credentials.Provided(_) =>
        validBasicAuthCredentials.find(user => user.username == p.identifier && p.verify(user.password))
      case _ => None
    }

  private def oAuthAuthenticator(credentials: Credentials): Option[LoggedInUser] =
    credentials match {
      case p @ Credentials.Provided(_) =>
        loggedInUsers.find(user => p.verify(user.oAuthToken.access_token))
      case _ => None
    }

  private def cleanUpExpiredUsers(): Unit =
    loggedInUsers
      .filter(user => user.loggedInAt.plusSeconds(user.oAuthToken.expires_in).isBefore(LocalDateTime.now()))
      .foreach(loggedInUsers -= _)

  case class BasicAuthCredentials(username: String, password: String)

  case class oAuthToken(access_token: String = java.util.UUID.randomUUID().toString,
                        token_type: String = "bearer",
                        expires_in: Int = 3600)

  case class LoggedInUser(basicAuthCredentials: BasicAuthCredentials,
                          oAuthToken: oAuthToken = new oAuthToken,
                          loggedInAt: LocalDateTime = LocalDateTime.now())

}
