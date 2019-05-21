package scala.camp.model
import java.time.LocalDateTime

case class User(id: Int, username: String, address: Option[String], email: String)

case class BasicAuthCredentials(username: String, password: String)

case class oAuthToken(access_token: String = java.util.UUID.randomUUID().toString,
                      token_type: String = "bearer",
                      expires_in: Int = 3600)

case class oAuthCredentials(id: Long,
                            username: String,
                            password: String,
                            accessToken: String,
                            tokenType: String,
                            expiresIn: Int,
                            loggedInAt: String)

case class LoggedInUser(basicAuthCredentials: BasicAuthCredentials,
                        oAuthToken: oAuthCredentials,
                        loggedInAt: LocalDateTime)
