package scala.camp.utils

class ExceptionExample(message: String) extends Exception(message) {

  def this(message: String, cause: Throwable) {
    this(message)
    initCause(cause)
  }

  def this(cause: Throwable) {
    this(Option(cause).map(_.toString).orNull, cause)
  }

  def this() {
    this(null: String)
  }
}

object ExceptionExample {

  def unapply(e: ExceptionExample): Option[(String, Throwable)] = Some((e.getMessage, e.getCause))

}
