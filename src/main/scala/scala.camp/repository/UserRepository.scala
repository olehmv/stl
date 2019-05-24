package scala.camp.repository
import java.sql.Date
import java.time.{LocalDate, LocalDateTime}
import java.time.format.DateTimeFormatter

import slick.jdbc.H2Profile.api._
import slick.lifted.Tag

import scala.camp.model.{User, UserAuth}
import scala.concurrent.{Await, Future}
import slick.jdbc.H2Profile.api._
import slick.lifted.Tag

import scala.camp.model.User
import scala.concurrent.duration._
import scala.concurrent.{Await, Future}
import scala.concurrent._
import ExecutionContext.Implicits.global
import scala.camp.UserApp.userAuthTable

trait UserRepository {

  lazy val db = Database.forConfig("database")
  lazy val userTable = TableQuery[UserTable]
  lazy val userAuthTable = TableQuery[UserAuthTable]
//  var u = userAuthTable.schema.create
//  Await.result(db.run(userAuthTable.schema.create), 2.seconds)

  class UserAuthTable(tag: Tag) extends Table[UserAuth](tag, "user_auth_table") {
    val userName = column[String]("user_name")
    val accessToken = column[String]("access_token")
    val tokenType = column[String]("token_type")
    val expiresIn = column[Int]("expires_in")
    val loggedInAt = column[String]("logged_in_at")
    def * =
      (userName, accessToken, tokenType, expiresIn, loggedInAt)
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
    db.run(userAuthTable.update(user))
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
      .collect {
        new PartialFunction[Int, Future[Int]] {
          def apply(id: Int) = db.run(userAuthTable += UserAuth(user.username, "", "", 0, ""))
          def isDefinedAt(id: Int) = { id != 0 }
        }
      }
      .flatMap(f => f)
  }
//  implicit val localDateToDate = MappedColumnType.base[LocalDateTime, Date](
//    l => Data.valueOf(l)
//    d => d.toLocalDate
//  )
//val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")
//  LocalDateTime.parse("", formatter)
//  val now=LocalDateTime.now()
//  val seq: Seq[UserAuth] = Await.result(db.run(userAuthTable.result),2.seconds)
//  seq.map(u=>u.copy(loggedInAt = LocalDateTime.parse(u.loggedInAt, formatter)))


//   def cleanUpExpiredUsers() ={
//     val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")
//     db.run(userAuthTable.filter{u=>
//     val r=  u.loggedInAt.result
//
//       r.
//       LocalDateTime.parse(u.loggedInAt.result,formatter)
//
//       val now=LocalDateTime.now()
//
//
//
//       true}.result)
//
//   }

}
