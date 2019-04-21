package vld

import org.scalatest.{BeforeAndAfter, FlatSpec, Matchers}

class ValidatorTest extends FlatSpec with Matchers with BeforeAndAfter {

  val positiveIntValidator: Validator[Int] = new Validator[Int] {
    def validate(input: Int): Either[String, Int] = if (input > 0) Right(input) else Left(s"$input not positive")
  }
  def lessThanIntValidator(n: Int): Validator[Int] = new Validator[Int] {
    override def validate(t: Int): Either[String, Int] = if (t < n) Right(t) else Left(s"$t not less than $n")
  }
  implicit class IntValidator(int: Int) {
    def validate(v: Validator[Int]): Either[String, Int] = {
      v.validate(int)
    }
  }
  "An Int Validator" should "validate positive Int input" in {
    positiveIntValidator.validate(1) should be(Right(1))
  }
  it should "not validate negative Int input" in {
    positiveIntValidator.validate(-1) should be(Left("-1 not positive"))
  }
  it should "compose two validators with  ' and '  operator and validate input" in {
    1 validate(positiveIntValidator and lessThanIntValidator(2)) should be(Right(1))
  }
  it should "compose two validators with  ' and '  operator and not validate input from first validator" in {
    -1 validate(positiveIntValidator and lessThanIntValidator(2)) should be(Left("-1 not positive"))
  }
  it should "compose two validators with  ' and '  operator and not validate input from second validator" in {
    1 validate(positiveIntValidator and lessThanIntValidator(0)) should be(Left("1 not less than 0"))
  }
  it should "compose two validators with  ' or '  operator and validate input" in {
    1 validate(positiveIntValidator or lessThanIntValidator(2)) should be(Right(1))
  }
  it should "compose two validators with  ' or '  operator and validate input from first validator" in {
    1 validate(positiveIntValidator or lessThanIntValidator(2)) should be(Right(1))
  }
  it should "compose two validators with  ' or '  operator and validate input from second validator"  in {
    -1 validate(positiveIntValidator or lessThanIntValidator(0)) should be(Right(-1))
  }
  it should "compose two validators with  ' or '  operator and not validate input from neither validator"  in {
    -1 validate(positiveIntValidator or lessThanIntValidator(-2)) should be(Left("-1 not positive and -1 not less than -2"))
  }

}
