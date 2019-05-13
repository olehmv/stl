package scala.camp.server

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.stream.{ActorMaterializer, Materializer}

import scala.camp.config.AppConfig
import scala.camp.service.UserService
import scala.concurrent.ExecutionContext

trait HttpServer extends AppConfig with UserService{
  implicit lazy val system: ActorSystem = ActorSystem()
  implicit lazy val materializer: Materializer = ActorMaterializer()
  implicit lazy val ec: ExecutionContext = system.dispatcher

  Http().bindAndHandle(userRouts, httpHost, httpPort)
}
