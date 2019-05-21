package scala.camp.utils

trait Validator[T] {
  self =>

  def validate(value: T): Either[String, T]

  def and(other: Validator[T]): Validator[T] = {
    new Validator[T] {
      override def validate(t: T): Either[String, T] = {
        self.validate(t) match {
          case Left(m)  => Left(m)
          case Right(t) => other.validate(t)
        }
      }
    }
  }

  def or(other: Validator[T]): Validator[T] = {
    new Validator[T] {
      override def validate(t: T): Either[String, T] = {
        self.validate(t) match {
          case Left(m1) =>
            other.validate(t) match {
              case Left(m2) => Left(s"$m1 and $m2")
              case Right(t) => Right(t)
            }
          case Right(t) => Right(t)
        }
      }
    }
  }
}
