package scala.camp.utils

import java.util.concurrent.TimeoutException

import org.scalatest.{BeforeAndAfter, FlatSpec, Matchers}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration._
import scala.concurrent.{Await, Future}

class RetryTest extends FlatSpec with Matchers with BeforeAndAfter with Retry {

  val initialTry= 1

  val retryZeroTimes= 0
  val retryTwoTimes = 2
  val retryTreeTimes= 3

  var retryListCount = 0


  before {
    retryListCount = 0
  }

  "A Retry " should "retry zero times when accepted result succeed from the first try" in {
    retry[Int](
      block =
        () => {
          retryListCount = retryListCount + 1
          retryListCount
        },
      acceptResult =
        res => true,
      retries =
        List(0.seconds, 1.seconds, 2.seconds))

    retryListCount - initialTry should be(retryZeroTimes)
  }
  it should "retry two times when accepted result succeed from the second retry" in {
    retry[Int](
      block =
        () => {
          retryListCount = retryListCount + 1;
          retryListCount
        },
      acceptResult =
        res => res - initialTry == retryTwoTimes,
      retries =
        List(0.seconds, 1.seconds, 2.seconds))

    retryListCount - initialTry should be(retryTwoTimes)
  }
  it should "retry three times when accepted result never succeed and retries list has tree elements" in {
    retry[Int](
      block =
        () => {
          retryListCount = retryListCount + 1;
          retryListCount
        },
      acceptResult =
        res => false,
      retries =
        List(0.seconds, 1.seconds, 2.seconds))

    retryListCount - initialTry should be(retryTreeTimes)
  }
  it should "back off 5 seconds" in{
    def rtr = retry[Int](() => {retryListCount = retryListCount + 1;retryListCount},(res:Int)=>false,List(5.seconds))
    val eventualInt = Future[Int](rtr)
    Await.result(eventualInt,5.seconds) should be(2)
  }

  it should "not back off more than 5 seconds" in{
    def rtr = retry[Int](() => {retryListCount = retryListCount + 1;retryListCount},(res:Int)=>false,List(6.seconds))
    val eventualInt = Future[Int](rtr)
    assertThrows[TimeoutException](Await.result(eventualInt,5.seconds))
  }
  it should "retry with future" in{
    def rtrf=retryf[Int](()=>Future(1),res=>false,List(1.seconds))
    Await.result(rtrf,2.seconds) should be(1)
  }

}
