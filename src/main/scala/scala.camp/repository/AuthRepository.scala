package scala.camp.repository
import java.time.LocalDateTime

import slick.jdbc.H2Profile.api._
import slick.lifted.Tag

import scala.camp.model.{BasicAuthCredentials, LoggedInUser, oAuthCredentials}
import scala.concurrent.duration._
import scala.concurrent.{Await, Future}

trait AuthRepository {

  lazy val db = Database.forConfig("database")
  lazy val oAuthTokenTable = TableQuery[oAuthTable]
  Await.result(db.run(oAuthTokenTable.schema.create), 2.seconds)

  class oAuthTable(tag: Tag) extends Table[oAuthCredentials](tag, "oAuthToken") {
    val id = column[Option[Long]]("id", O.PrimaryKey, O.AutoInc)
    val userName = column[String]("username")
    val password = column[String]("password")
    val accessToken = column[Option[String]]("access_token")
    val tokenType = column[Option[String]]("token_type")
    val expiresIn = column[Option[Int]]("expires_in")
    val loggedInAt = column[Option[String]]("logged_in_at")
    def * =
      (id, userName, password, accessToken, tokenType, expiresIn, loggedInAt)
        .mapTo[oAuthCredentials]
  }

  def addBasicAuthCredentials(credentials: BasicAuthCredentials):Future[Int] = {
    db.run(
      oAuthTokenTable += oAuthCredentials(id = 0,
                                          username = credentials.username,
                                          password = credentials.password,
                                          accessToken = "",
                                          tokenType = "",
                                          expiresIn = 0,
                                          loggedInAt = ""))

  }

  def findBasicAuthCredentials(id:Int): Future[Option[oAuthCredentials]] = {
    db.run(
      oAuthTokenTable
        .filter(cred =>
          cred.id==id)
        .result
        .headOption
    )
  }

  def authenticateUser(credentials: oAuthCredentials) = {
    db.run(
      oAuthTokenTable.insertOrUpdate(credentials)
    )
  }

}
