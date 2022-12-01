package day1

import zio.*
import zio.stream.*
import zio.stream.ZPipeline.*

import java.io.IOException
import java.nio.file.Paths
import scala.collection.immutable.TreeSet
import scala.util.Try

object Solution extends ZIOAppDefault {

  val numberOfBest = 3

  case class Result(
      maxCalories: TreeSet[Int],
      intermediateCalories: Int
  )

  object Result {
    val init: Result = Result(TreeSet.empty, 0)
  }

  val run = ZStream
    .fromResource("day1.txt")
    .via(utf8Decode >>> splitLines)
    .runFold(Result.init) { (result, line) =>
      line match
        case "" =>
          Result((result.maxCalories + result.intermediateCalories).takeRight(numberOfBest), 0)
        case Int(calories) =>
          result.copy(intermediateCalories = result.intermediateCalories + calories)
        case _ =>
          throw new RuntimeException(s"Invalid line: $line")
    }
    .tap(result => Console.printLine(s"${result.maxCalories.sum}"))

  object Int {
    def unapply(s: String): Option[Int] = Try(s.toInt).toOption
  }
}
