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

object Main extends AppConfig {
  def main(args: Array[String]): Unit = {
    UserApp.startServer(httpHost,httpPort)
  }
}

object UserApp extends HttpApp with UserRoutes{

  implicit lazy val system: ActorSystem = ActorSystem()
  implicit lazy val materializer: Materializer = ActorMaterializer()
  implicit lazy val ec: ExecutionContext = system.dispatcher
  override def postHttpBinding(binding: Http.ServerBinding): Unit = {
    systemReference.get().scheduler.schedule(1 minutes, 1 minutes)(cleanUpExpiredUsers())(systemReference.get().dispatcher)
    super.postHttpBinding(binding)
  }
//  http post :8080/user id:=0 username=ivan password=ivan email=email@em.com
//  http -a ivan:ivan post :8080/user/auth
//  http :8080/user?id=1  "authorization: Bearer b946ff90-555c-4404-b499-1725ea1c72a4"
  override protected def routes: Route = userRoutes
}
