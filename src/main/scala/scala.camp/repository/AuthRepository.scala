//package scala.camp.repository
//import java.time.LocalDateTime
//
//import akka.actor.Status.Success
//import akka.http.scaladsl.server.directives.Credentials
//import slick.jdbc.H2Profile.api._
//import slick.lifted.Tag
//
//import scala.camp.model.{BasicAuthCredentials, LoggedInUser, UserAuth}
//import scala.concurrent.duration._
//import scala.concurrent.{Await, Future}
//import scala.concurrent._
//import ExecutionContext.Implicits.global
//import scala.util.Try
////https://www.jannikarndt.de/blog/2018/10/oauth2-akka-http/
//trait AuthRepository {
//
//  lazy val authDb = Database.forConfig("database")
//  lazy val userAuthTable = TableQuery[userAuthTable]
//  Await.result(authDb.run(userAuthTable.schema.create), 2.seconds)
//
//  class userAuthTable(tag: Tag) extends Table[UserAuth](tag, "user_auth_table") {
//    val userName = column[String]("user_name")
//    val accessToken = column[String]("access_token")
//    val tokenType = column[String]("token_type")
//    val expiresIn = column[Int]("expires_in")
//    val loggedInAt = column[String]("logged_in_at")
//    def * =
//      (userName, accessToken, tokenType, expiresIn, loggedInAt)
//        .mapTo[UserAuth]
//  }
//
//  def addUser(user: UserAuth) = {
//    authDb.run(userAuthTable += user)
//  }
//  def findUser(userName: String): Future[Option[UserAuth]] = {
//    authDb.run(userAuthTable.filter(user => user.userName === userName).result.headOption)
//  }
//
//  def authenticateUser(user: UserAuth) = {
//    authDb.run(userAuthTable returning userAuthTable.map(_.userName) += user)
//  }
//
////  private def oAuthAuthenticator(credentials: Credentials): Option[UserAuth] =
////    credentials match {
////      case p @ Credentials.Provided(_) =>
////        val f: Future[Option[UserAuth]] = findUser(p.identifier.toInt)
////        var maybeUser: Option[UserAuth] = None
////        f.onComplete { t: Try[Option[UserAuth]] =>
////          t.get match {
////            case Some(user) =>
////              if (p.verify(user.accessToken)) {
////                maybeUser = Some(user)
////              } else {
////                None
////              }
////            case None => None
////          }
////        }
////        maybeUser
////      case _ => None
////    }
////
////  private def BasicAuthAuthenticator(credentials: Credentials): Option[UserAuth] =
////    credentials match {
////      case p @ Credentials.Provided(_) =>
////        val f: Future[Option[UserAuth]] = findUser(p.identifier.toInt)
////        var maybeUser: Option[UserAuth] = None
////        f.onComplete { t: Try[Option[UserAuth]] =>
////          t.get match {
////            case Some(user) =>
////              if (p.verify(user.password)) {
////                maybeUser = Some(user)
////              } else {
////                None
////              }
////            case None => None
////          }
////        }
////        maybeUser
////      case _ => None
////    }
//}
