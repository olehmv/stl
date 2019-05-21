package scala.camp.utils
import org.scalatest.{BeforeAndAfterAll, FlatSpec, Matchers}

import scala.camp.model.User
import scala.camp.repository.UserRepositoryImpl
import scala.concurrent.{Await, Future}
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration._
class UserRepositoryTest
    extends FlatSpec
    with Matchers
    with BeforeAndAfterAll
    with UserRepositoryImpl {

  var user: User = null

  override def beforeAll(): Unit = {
    super.beforeAll()
    user = new User(1, "Kolya", Some("Lviv"), "kolya@mail.com")

  }

  "A UserRepository " should " register user" in {
    Await.result(registerUser(user),2.seconds) should be(1)
  }
  it should "find user by user name" in {
    Await.result(getByUsername("Kolya"),2.seconds) should be(Seq(user))
  }
  it should "find user by user id" in {
    Await.result(getById(user.id),2.seconds) should be(Some(user))
  }

}
