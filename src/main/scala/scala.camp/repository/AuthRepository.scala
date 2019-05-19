package scala.camp.repository
import java.time.LocalDateTime

import slick.jdbc.H2Profile.api._
import slick.lifted.Tag

import scala.camp.model.{BasicAuthCredentials, LoggedInUser, oAuthToken}
import scala.concurrent.duration._
import scala.concurrent.{Await, Future}

trait AuthRepository {

  lazy val db = Database.forConfig("database")
  lazy val basicAuthCredentialsTable = TableQuery[BasicAuthCredentialsTable]
  lazy val oAuthTokenTable = TableQuery[oAuthTokenTable]
  Await.result(db.run(basicAuthCredentialsTable.schema.create), 2.seconds)
  Await.result(db.run(oAuthTokenTable.schema.create), 2.seconds)

  class BasicAuthCredentialsTable(tag: Tag)
      extends Table[BasicAuthCredentials](tag, "BasicAuthCredentials") {
    val userName = column[String]("username")
    val password = column[String]("password")

    def * = (userName, password).mapTo[BasicAuthCredentials]
  }

  class oAuthTokenTable(tag: Tag) extends Table[oAuthToken](tag, "oAuthToken") {
    val accessToken = column[String]("access_token")
    val tokenType = column[String]("token_type")
    val expiresIn = column[String]("expires_in")
    val loggedInAt = column[String]("logged_in_at")
    def * = (accessToken, tokenType, expiresIn, loggedInAt).mapTo[oAuthToken]
  }

  def findBasicAuthCredentials(userName: String,
                               password: String): Future[Option[BasicAuthCredentials]] = {
    db.run(
      basicAuthCredentialsTable
        .filter(cred => cred.userName == userName && cred.password == password)
        .result
        .headOption)
  }

  def addLoggedInUser(user: LoggedInUser) = {
    val credentials = user.basicAuthCredentials
    val oAuthToken = user.oAuthToken
    db.run(basicAuthCredentialsTable += credentials)
    db.run(oAuthTokenTable += oAuthToken)
  }

}
