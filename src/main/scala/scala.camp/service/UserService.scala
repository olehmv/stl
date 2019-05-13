package scala.camp.service

import scala.camp.routes.UserRoutes
import scala.concurrent.ExecutionContext

trait UserService{

  implicit val executionContext=ExecutionContext.fromExecutor(
    new java.util.concurrent.ForkJoinPool(2)
  )
  def userRouts= new UserRoutes().routes
}
