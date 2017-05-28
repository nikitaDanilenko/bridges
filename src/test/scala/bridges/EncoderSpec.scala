package bridges

import org.scalatest._

object EncoderSpec {
  // Sample product
  case class Pair(a: String, b: Int)

  // Sample coproduct
  sealed abstract class OneOrOther extends Product with Serializable
  case class One(value: String) extends OneOrOther
  case class Other(value: Int) extends OneOrOther

  // Sample value class
  case class Value(value: String) extends AnyVal

  // ADT with intermediate type appearing more than once:
  final case class Color(red: Int, green: Int, blue: Int)
  sealed abstract class Shape extends Product with Serializable
  final case class Circle(radius: Double, color: Color) extends Shape
  final case class Rectangle(width: Double, height: Double, color: Color) extends Shape
}

class EncoderSpec extends FreeSpec with Matchers {
  import syntax._
  import EncoderSpec._
  import Type._

  "encode[A]" - {
    "primitive types" in {
      encode[String] should be(Str)
      encode[Int] should be(Num)
      encode[Double] should be(Num)
      encode[Boolean] should be(Bool)
    }

    "options" in {
      encode[Option[String]] should be(Str | Null)
      encode[Option[Int]] should be(Num | Null)
    }

    "sequences" in {
      encode[Seq[String]] should be(Array(Str))
      encode[List[Boolean]] should be(Array(Bool))
      encode[Set[Int]] should be(Array(Num))
    }

    "value classes" in {
      encode[Value] should be(Str)
    }

    "case classes" in {
      encode[Pair] should be(Struct("a" -> Str, "b" -> Num))
    }

    "sealed types" in {
      encode[OneOrOther] should be(DiscUnion("One" -> Ref("One"), "Other" -> Ref("Other")))
    }
  }

  "declaration[A]" - {
    "value classes" in {
      declaration[Value] should be("Value" := Str)
    }

    "case classes" in {
      declaration[Pair] should be("Pair" := Struct("a" -> Str, "b" -> Num))
    }

    "sealed types" in {
      declaration[OneOrOther] should be("OneOrOther" := DiscUnion("One" -> Ref("One"), "Other" -> Ref("Other")))
    }
  }
}
