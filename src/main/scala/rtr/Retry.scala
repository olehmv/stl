package rtr

import scala.annotation.tailrec
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


}
