package vld

/**
  * Implement validator typeclass that should validate arbitrary value [T].
  *
  * @tparam T the type of the value to be validated.
  */
trait Validator[T] {
  self =>
  /**
    * Validates the value.
    *
    * @param value value to be validated.
    * @return Right(value) in case the value is valid, Left(message) on invalid value
    */
  def validate(value: T): Either[String, T]

  /**
    * And combinator.
    *
    * @param other validator to be combined with 'and' with this validator.
    * @return the Right(value) only in case this validator and <code>other</code> validator returns valid value,
    *         otherwise Left with error messages from both validators.
    */
  def and(other: Validator[T]): Validator[T] = {
    new Validator[T] {
      override def validate(t: T): Either[String, T] = {
        self.validate(t) match {
          case Left(m) => Left(m)
          case Right(t) => other.validate(t)
        }
      }
    }
  }

  /**
    * Or combinator.
    *
    * @param other validator to be combined with 'or' with this validator.
    * @return the Right(value) only in case either this validator or <code>other</code> validator returns valid value,
    *         otherwise Left with error messages from the failed validator or from both if both failed.
    */
  def or(other: Validator[T]): Validator[T] = {
    new Validator[T] {
      override def validate(t: T): Either[String, T] = {
        self.validate(t) match {
          case Left(m1) => other.validate(t) match {
            case Left(m2)=>Left(s"$m1 and $m2")
            case Right(t)=>Right(t)
          }
          case Right(t) => Right(t)
        }
      }
    }
  }
}


object Validator {

  implicit class PersonValidator(person: Person) {
    def validate(v: Validator[Person]): Either[String, Person] = {
      v.validate(person)
    }
  }

  implicit class DefaultPersonValidator(person: Person) {
    def validate: Either[String, Person] = {
      isPersonValid.validate(person)
    }
  }

  implicit class StringValidator(string: String) {
    def validate(v: Validator[String]): Either[String, String] = {
      v.validate(string)
    }
  }

  implicit class DefaultStringValidator(string: String) {
    def validate: Either[String, String] = {
      nonEmpty.validate(string)
    }
  }

  implicit class IntValidator(int: Int) {
    def validate(v: Validator[Int]): Either[String, Int] = {
      v.validate(int)
    }
  }

  implicit class DefaultIntValidator(int: Int) {
    def validate: Either[String, Int] = {
      positiveInt.validate(int)
    }
  }


  val positiveInt: Validator[Int] = new Validator[Int] {
    // implement me
    override def validate(t: Int): Either[String, Int] = if (t > 0) Right(t) else Left(s"not positive $t")

  }
  val negativeInt: Validator[Int] = new Validator[Int] {
    // implement me
    override def validate(t: Int): Either[String, Int] = if (t < 0) Right(t) else Left(s"not negative $t")

  }

  def lessThan(n: Int): Validator[Int] = new Validator[Int] {
    // implement me
    override def validate(t: Int): Either[String, Int] = if (t < n) Right(t) else Left(s"$t not less than $n")
  }

  def greaterThan(n: Int): Validator[Int] = new Validator[Int] {
    // implement me
    override def validate(t: Int): Either[String, Int] = if (t > n) Right(t) else Left(s"$t not greater than $n")
  }

  val nonEmpty: Validator[String] = new Validator[String] {
    // implement me
    override def validate(t: String): Either[String, String] = if (t.isEmpty) Left("empty string") else Right(t)
  }

  val isPersonValid = new Validator[Person] {
    // implement me
    // Returns valid only when the name is not empty and age is in range [1-99].
    override def validate(value: Person): Either[String, Person] = if (value.name.isEmpty || {
      value.age match {
        case a if 1 until 100 contains a => false
        case _ => true
      }
    }) Left("empty name and age not in range [1-99]") else Right(value)
  }
}

object ValidApp extends App {

  import Validator.{IntValidator, PersonValidator, StringValidator, isPersonValid, lessThan,greaterThan, positiveInt}
  // uncomment make possible next code to compile
  -2 validate (positiveInt and lessThan(10)) match {
    case Left(m) => println(m)
    case Right(r) => println(r)
  }
  20 validate (positiveInt and lessThan(10)) match {
    case Left(m) => println(m)
    case Right(r) => println(r)
  }

  -2 validate (greaterThan(10) or positiveInt) match {
    case Left(m) => println(m)
    case Right(r) => println(r)
  }

  // uncomment make possible next code to compile
  "" validate Validator.nonEmpty match {
    case Left(m) => println(m)
    case Right(r) => println(r)
  }

  // uncomment make possible next code to compile
  Person(name = "John", age = 255) validate isPersonValid match {
    case Left(m) => println(m)
    case Right(r) => println(r)
  }
}

object ImplicitValidApp extends App {

  import Validator.{DefaultIntValidator, DefaultPersonValidator, DefaultStringValidator}

  // uncomment next code and make it compilable and workable
  Person(name = "John", age = 25) validate match {
    case Left(m) => println(m)
    case Right(r) => println(r)
  }
  "asdasd" validate match {
    case Left(m) => println(m)
    case Right(r) => println(r)
  }
  234.validate match {
    case Left(m) => println(m)
    case Right(r) => println(r)
  }
}

case class Person(name: String, age: Int)
