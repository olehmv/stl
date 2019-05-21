package scala.camp

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.stream.{ActorMaterializer, Materializer}

import scala.camp.config.AppConfig
import scala.camp.routes.UserRoutes
import scala.concurrent.ExecutionContext

object UserApp extends App with UserRoutes with AppConfig{

  implicit lazy val system: ActorSystem = ActorSystem()
  implicit lazy val materializer: Materializer = ActorMaterializer()
  implicit lazy val ec: ExecutionContext = system.dispatcher

  Http().bindAndHandle(routes, httpHost, httpPort)

}
