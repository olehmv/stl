package scala.camp.repository

import slick.jdbc.H2Profile.api._
import slick.lifted.Tag

import scala.camp.model.User
import scala.concurrent.Future

trait UserRepository[F[_]] {

  def registerUser(user: User): F[Int]

  def getById(id: Int): F[Option[User]]

  def getByUsername(username: String): F[Seq[User]]
}

trait UserRepositoryImpl extends UserRepository[Future] {

  lazy val db = Database.forConfig("database")
  lazy val userTable = TableQuery[UserTable]

  class UserTable(tag: Tag) extends Table[User](tag, "users") {
    val id = column[Int]("id", O.PrimaryKey, O.AutoInc)
    val userName = column[String]("username")
    val address = column[Option[String]]("address")
    val email = column[String]("email")

    def * = (id, userName, address, email).mapTo[User]
  }

  def registerUser(user: User): Future[Int] = {
    db.run(userTable returning userTable.map(_.id) += user)
  }

  def getByUsername(username: String): Future[Seq[User]] = {
    db.run(userTable.filter(e => e.userName === username).result)
  }

  def getById(id: Int): Future[Option[User]] = {
    db.run(userTable.filter(e => e.id === id).result.headOption)
  }

}
