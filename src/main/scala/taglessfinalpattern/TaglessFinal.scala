package taglessfinalpattern

import cats.{Id, Monad}

import scala.concurrent.Future

/**
  * Repository and Service implementation using tagless final pattern.
  * The idea is to make it easier to test our database layer, using Scala’s higher kinded types to abstract
  * the Future type constructor away from our traits under test.
  * Intro to tagless final: https://www.basementcrowd.com/2019/01/17/an-introduction-to-tagless-final-in-scala/.
  * The similar task example https://github.com/LvivScalaClub/cats-playground/blob/master/src/main/scala/BookRepository.scala
  */

case class User(id: Long, username: String)

case class IotDevice(id: Long, userId: Long, sn: String)

// NOTE: This import bring into the scope implicits that allow you to call .map and .flatMap on the type F[_]
// and also bring you typeclasses that know how to flatmap (Monad) and map (Functor) over your higher-kinded type.
import cats.implicits._

trait UserRepository[F[_]] {
  def registerUser(username: String): F[User]

  def getById(id: Long): F[Option[User]]

  def getByUsername(username: String): F[Option[User]]
}

class UserRepositoryID extends UserRepository[Id] {

  private var repository: List[User] = List()
  private var id: Long = 0

  override def registerUser(username: String): Id[User] = repository.find(user => user.username == username) match {
    case None => {
      id = id + 1
      val user = User(id, username)
      repository = user :: repository
      user
    }
    case Some(user) => user
  }

  override def getById(id: Long): Id[Option[User]] = repository.find(user => user.id == id) match {
    case None => None
    case Some(user) => Some(user)
  }

  override def getByUsername(username: String): Id[Option[User]] = repository.find(user => user.username == username) match {
    case None => None
    case Some(user) => Some(user)
  }
}

class UserRespositoryFuture extends UserRepository[Future] {

  private var repository: List[User] = List()
  private var id: Long = 0

  override def registerUser(username: String): Future[User] = repository.find(user => user.username == username) match {
    case None => {
      id = id + 1
      val user = User(id, username)
      repository = user :: repository
      Future.successful(user)
    }
    case Some(user) => Future.successful(user)
  }

  override def getById(id: Long): Future[Option[User]] = repository.find(user => user.id == id) match {
    case None => Future.successful(None)
    case Some(user) => Future.successful(Some(user))
  }

  override def getByUsername(username: String): Future[Option[User]] = repository.find(user => user.username == username) match {
    case None => Future.successful(None)
    case Some(user) => Future.successful(Some(user))
  }
}

trait IotDeviceRepository[F[_]] {
  def registerDevice(userId: Long, serialNumber: String): F[IotDevice]

  def getById(id: Long): F[Option[IotDevice]]

  def getBySn(sn: String): F[Option[IotDevice]]

  def getByUser(userId: Long): F[Seq[IotDevice]]
}

class IotDeviceRepositoryId extends IotDeviceRepository[Id] {

  private var repository: List[IotDevice] = List()
  private var id: Long = 0

  override def registerDevice(userId: Long, serialNumber: String): Id[IotDevice] = repository.find(device => device.sn == serialNumber) match {
    case _ => {
      id = id + 1
      val device = IotDevice(id, userId, serialNumber)
      repository = device :: repository
      device
    }
  }

  override def getById(id: Long): Id[Option[IotDevice]] = repository.find(device => device.id == id) match {
    case None => None
    case Some(device) => Some(device)
  }

  override def getBySn(sn: String): Id[Option[IotDevice]] = repository.find(device => device.sn == sn) match {
    case None => None
    case Some(device) => Some(device)
  }

  override def getByUser(userId: Long): Id[Seq[IotDevice]] = repository.filter(device => device.userId == userId)
}


class IotDeviceRepositoryFuture extends IotDeviceRepository[Future] {

  private var repository: List[IotDevice] = List()
  private var id: Long = 0

  override def registerDevice(userId: Long, serialNumber: String): Future[IotDevice] = repository.find(device => device.sn == serialNumber) match {
    case _ => {
      id = id + 1
      val device = IotDevice(id, userId, serialNumber)
      repository = device :: repository
      Future.successful(device)
    }
  }

  override def getById(id: Long): Future[Option[IotDevice]] = repository.find(device => device.id == id) match {
    case None => Future.successful(None)
    case Some(device) => Future.successful(Option(device))
  }

  override def getBySn(sn: String): Future[Option[IotDevice]] = repository.find(device => device.sn == sn) match {
    case None => Future.successful(None)
    case Some(device) => Future.successful(Option(device))
  }

  override def getByUser(userId: Long): Future[Seq[IotDevice]] = Future.successful(repository.filter(device => device.userId == userId))
}


class UserService[F[_]](repository: UserRepository[F])
                       (implicit monad: Monad[F]) {

  def registerUser(username: String): F[Either[String, User]] = {
    // .flatMap syntax works because of import cats.implicits._
    // so flatMap function is added to F[_] through implicit conversions
    // The implicit monad param knows how to flatmap and map over your F.
    repository.getByUsername(username).flatMap({
      case Some(user) =>
        monad.pure(Left(s"User $user already exists"))
      case None =>
        // .map syntax works because of import cats.implicits._
        // so map function is added to F[_] through implicit conversions
        repository.registerUser(username).map(Right(_))
    })
  }

  def getByUsername(username: String): F[Option[User]] = repository.getByUsername(username)

  def getById(id: Long): F[Option[User]] = repository.getById(id)
}

class IotDeviceService[F[_]](repository: IotDeviceRepository[F],
                             userRepository: UserRepository[F])
                            (implicit monad: Monad[F]) {

  // the register should fail with Left if the user doesn't exist or the sn already exists.
  def registerDevice(userId: Long, sn: String): F[Either[String, IotDevice]] = {
    userRepository.getById(userId).flatMap({
      case Some(user) =>
        val r: F[Either[String, IotDevice]] = repository.getBySn(sn).flatMap {
          case Some(d) => monad.pure(Left(s"Device $d already exists"))
          case None => repository.registerDevice(userId, sn).map(Right(_))
        }
        r
      case None =>
        monad.pure(Left(s"User with id: $userId does not exists"))

    })
  }
}

// task1: implement in-memory Respository with Id monad.
// task2: implement in-memory Respository with Future monad
// example https://github.com/LvivScalaClub/cats-playground/blob/master/src/main/scala/BookRepository.scala

// task3: unit tests
