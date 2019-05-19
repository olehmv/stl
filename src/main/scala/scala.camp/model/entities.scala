package scala.camp.model
import java.time.LocalDateTime

case class User(id: Int, username: String, address: Option[String], email: String)

case class BasicAuthCredentials(username: String, password: String)

case class oAuthToken(access_token: String, token_type: String, expires_in: Int, loggedInAt: String)

case class LoggedInUser(basicAuthCredentials: BasicAuthCredentials,
                        oAuthToken: oAuthToken,
                        loggedInAt: LocalDateTime)
