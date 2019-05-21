package scala.camp.repository
import slick.jdbc.H2Profile.api._
import slick.lifted.Tag

import scala.camp.model.User
import scala.concurrent.duration._
import scala.concurrent.{Await, Future}

import slick.jdbc.H2Profile.api._
import slick.lifted.Tag

import scala.camp.model.User
import scala.concurrent.duration._
import scala.concurrent.{Await, Future}

trait UserRepository {

  lazy val db = Database.forConfig("database")
  lazy val userTable = TableQuery[UserTable]
  val s = userTable.schema.create
//  Await.result(db.run(userTable.schema.create), 2.seconds)

  class UserTable(tag: Tag) extends Table[User](tag, "users") {
    val id = column[Option[Int]]("id", O.PrimaryKey, O.AutoInc)
    val userName = column[String]("username")
    val address = column[Option[String]]("address")
    val email = column[String]("email")

    def * = (id, userName, address, email).mapTo[User]
  }

  def getById(id: Int): Future[Option[User]] = {
    db.run(userTable.filter(e => e.id === id).result.headOption)
  }

  def registerUser(user: User): Future[Int] = {
    db.run(userTable += user)
  }

}
