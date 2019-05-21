package scala.camp

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.server.{HttpApp, Route}
import akka.stream.{ActorMaterializer, Materializer}

import scala.camp.config.AppConfig
import scala.camp.routes.UserRoutes
import scala.concurrent.ExecutionContext

object UserApp extends HttpApp with UserRoutes with AppConfig{

  implicit lazy val system: ActorSystem = ActorSystem()
  implicit lazy val materializer: Materializer = ActorMaterializer()
  implicit lazy val ec: ExecutionContext = system.dispatcher

  def userRouts = userRoutes

  def main(args: Array[String]): Unit = {
    Http().bindAndHandle(userRouts, httpHost, httpPort)

  }

  override protected def routes: Route = userRouts
}
