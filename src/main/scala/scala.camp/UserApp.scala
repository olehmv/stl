package scala.camp

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.server.{HttpApp, Route}
import akka.stream.{ActorMaterializer, Materializer}

import scala.camp.config.AppConfig
import scala.camp.routes.UserRoutes
import scala.concurrent.ExecutionContext
import scala.concurrent.duration._

object UserApp extends HttpApp with UserRoutes with AppConfig {

  implicit lazy val system: ActorSystem = ActorSystem()
  implicit lazy val materializer: Materializer = ActorMaterializer()
  implicit lazy val ec: ExecutionContext = system.dispatcher

  def main(args: Array[String]): Unit = {
    Http().bindAndHandle(userRoutes, httpHost, httpPort)
  }
  override def postHttpBinding(binding: Http.ServerBinding): Unit = {
    systemReference.get().scheduler.schedule(5 minutes, 5 minutes)(cleanUpExpiredUsers())(systemReference.get().dispatcher)
    super.postHttpBinding(binding)
  }



  }

//      .filter(user => user.loggedInAt
//        .plusSeconds(user.oAuthToken.expires_in)
//        .isBefore(LocalDateTime.now()))
//      .foreach(loggedInUsers -= _)


  override protected def routes: Route = userRoutes
}
