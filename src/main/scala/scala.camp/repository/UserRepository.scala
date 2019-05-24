package scala.camp.repository

import scala.camp.model.UserAuth
import slick.jdbc.H2Profile.api._
import slick.lifted.Tag

import scala.camp.model.User
import scala.concurrent.Future
import scala.concurrent._
import ExecutionContext.Implicits.global

trait UserRepository {

  lazy val db = Database.forConfig("database")
  lazy val userTable = TableQuery[UserTable]
  lazy val userAuthTable = TableQuery[UserAuthTable]

  class UserAuthTable(tag: Tag) extends Table[UserAuth](tag, "user_auth_table") {
    val accessToken = column[String]("access_token")
    val tokenType = column[String]("token_type")
    val expiresIn = column[Int]("expires_in")
    val loggedInAt = column[String]("logged_in_at")
    def * =
      (accessToken, tokenType, expiresIn, loggedInAt)
        .mapTo[UserAuth]
  }
  class UserTable(tag: Tag) extends Table[User](tag, "users") {
    val id = column[Int]("id", O.PrimaryKey, O.AutoInc)
    val userName = column[String]("username")
    val password = column[String]("password")
    val email = column[String]("email")

    def * = (id, userName, password, email).mapTo[User]
  }

  def authenticateUser(user: UserAuth) = {
    db.run(userAuthTable+=(user))
  }

  def findUser(accessToken: String): Future[Option[UserAuth]] = {
    db.run(userAuthTable.filter(user => user.accessToken === accessToken).result.headOption)
  }

  def getById(id: Int): Future[Option[User]] = {
    db.run(userTable.filter(e => e.id === id).result.headOption)
  }

  def getByName(userName: String): Future[Option[User]] = {
    db.run(userTable.filter(e => e.userName === userName).result.headOption)
  }

  def registerUser(user: User): Future[Int] = {
    db.run(userTable returning userTable.map(_.id) += user)
  }
  def cleanUpExpiredUsers(): Unit ={
    db.run(userAuthTable.delete)
  }
}
