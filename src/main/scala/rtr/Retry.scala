package rtr

import scala.annotation.tailrec
import scala.concurrent.Future
import scala.concurrent.duration.FiniteDuration

object Retry extends App {

  @tailrec
  def retry[A](block: () => A, acceptResult: A => Boolean, retries: List[FiniteDuration]): A = {

    val a = block()
    if (acceptResult(a)) {
      a
    } else {
      retries match {
        case Nil => a
        case x :: xs => {
          Thread.sleep(x.toMillis)
          retry(block, acceptResult, xs)
        }
      }
    }
  }

  def retryf[A](block: () => Future[A], acceptResult: Future[A] => Boolean, retries: List[FiniteDuration]): Future[A] = {
    val fa = block()
    if (acceptResult(fa) || retries.isEmpty) {
      fa
    } else {
      Thread.sleep(retries.head.toMillis)
      retryf(block, acceptResult, retries.tail)
    }
  }

}
