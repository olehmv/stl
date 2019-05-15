package taglessfinalpattern

import cats.{Id, Monad}
import org.scalatest.{BeforeAndAfterAll, FlatSpec, Matchers}

class TaglessFinalTest extends FlatSpec with Matchers with BeforeAndAfterAll {


  var userRepositoryID: UserRepositoryID = null
  var iotDeviceRepositoryId: IotDeviceRepositoryId = null
  var userService: UserService[Id] = null
  var iotDeviceService: IotDeviceService[Id] = null
  var user: User = null
  var device: IotDevice = null

  override def beforeAll(): Unit = {
    super.beforeAll()
    userRepositoryID = new UserRepositoryID()
    iotDeviceRepositoryId = new IotDeviceRepositoryId()
    userService = new UserService[Id](userRepositoryID)(Monad[Id])
    iotDeviceService = new IotDeviceService[Id](iotDeviceRepositoryId, userRepositoryID)(Monad[Id])
    user = new User(1, "Kolya")
    userService.registerUser("Kolya")
    device = new IotDevice(1, 1, "0001")
    iotDeviceService.registerDevice(1, "0001")

  }

  "A Tagless Final Pattern Service" should " register user" in {
    val maybeUser: Id[Either[String, User]] = userService.registerUser("Bodya")
    maybeUser.right.get should be(user.copy(id = 2, username = "Bodya"))
  }
  it should "find user by user name" in {
    val maybeUser: Id[Option[User]] = userService.getByUsername("Kolya")
    maybeUser.get should be(user)
  }
  it should "find user by user id" in {
    val maybeUser: Id[Option[User]] = userService.getById(user.id)
    maybeUser.get should be(user)
  }
  it should "register device" in {
    val maybeDevice: Id[Either[String, IotDevice]] = iotDeviceService.registerDevice(1, "0002")
    maybeDevice.right.get should be(device.copy(id=2,sn = "0002"))
  }
  it should "fail register device when user does not exists" in{
    val maybeDevice: Id[Either[String, IotDevice]] = iotDeviceService.registerDevice(10, "0002")
    maybeDevice.left.get should be("User with id: 10 does not exists")
  }
  it should "fail register device when device already exists" in{
    val maybeDevice: Id[Either[String, IotDevice]] = iotDeviceService.registerDevice(1, "0001")
    maybeDevice.left.get should be("Device IotDevice(1,1,0001) already exists")
  }

}
