package day2

import cats.kernel.{Comparison, Order}
import cats.implicits.*
import zio.*
import zio.stream.*
import zio.stream.ZPipeline.*

import java.io.IOException

object Solution extends ZIOAppDefault:

  val loadLines: String => ZStream[Any, IOException, String] = path =>
    ZStream.fromResource(path).via(utf8Decode >>> splitLines)

  enum Move(val value: Int):
    case Rock extends Move(1)
    case Paper extends Move(2)
    case Scissors extends Move(3)

  object Move:

    def calculateYourMove(opponentMove: Move, comparison: Comparison): Move =
      comparison match
        case Comparison.EqualTo => opponentMove
        case Comparison.GreaterThan =>
          opponentMove match
            case Move.Rock     => Move.Paper
            case Move.Paper    => Move.Scissors
            case Move.Scissors => Move.Rock
        case Comparison.LessThan =>
          opponentMove match
            case Move.Rock     => Move.Scissors
            case Move.Paper    => Move.Rock
            case Move.Scissors => Move.Paper

    given Order[Move] = (x: Move, y: Move) =>
      (x, y) match
        case (Rock, Rock)         => 0
        case (Rock, Paper)        => 1
        case (Rock, Scissors)     => -1
        case (Paper, Rock)        => -1
        case (Paper, Paper)       => 0
        case (Paper, Scissors)    => 1
        case (Scissors, Rock)     => 1
        case (Scissors, Paper)    => -1
        case (Scissors, Scissors) => 0

    def score(opponentMove: Move, yourMove: Move): Int =
      (opponentMove.comparison(yourMove).toInt + 1) * 3 + yourMove.value

    def fromString(s: String): Option[Move] = s match
      case "A" => Some(Rock)
      case "X" => Some(Rock)
      case "B" => Some(Paper)
      case "Y" => Some(Paper)
      case "C" => Some(Scissors)
      case "Z" => Some(Scissors)
      case _   => None

  val makeComparison: String => Option[Comparison] =
    case "X" => Some(Comparison.LessThan)
    case "Y" => Some(Comparison.EqualTo)
    case "Z" => Some(Comparison.GreaterThan)
    case _   => None

  val firstStar: ZIO[Any, Exception, Int] = loadLines("day2.txt")
    .map(_.split("\\s"))
    .flatMap {
      case Array(opponentMove, yourMove) =>
        Move
          .fromString(opponentMove)
          .zip(Move.fromString(yourMove))
          .map(ZStream.succeed(_))
          .getOrElse(ZStream.fail(new Exception("Invalid input")))
      case _ =>
        ZStream.fail(new Exception("Invalid input"))
    }
    .map(Move.score.tupled)
    .runSum
    .tap(scoreSum => Console.printLine(s"Score: ${scoreSum}"))

  val secondStar = loadLines("day2.txt")
    .map(_.split("\\s"))
    .flatMap {
      case Array(opponentMove, comparison) =>
        Move
          .fromString(opponentMove)
          .zip(makeComparison(comparison))
          .map(ZStream.succeed(_))
          .getOrElse(ZStream.fail(new Exception("Invalid input")))
      case _ =>
        ZStream.fail(new Exception("Invalid input"))
    }
    .map { case (opponentMove, comparison) =>
      Move.score(opponentMove, Move.calculateYourMove(opponentMove, comparison))
    }
    .runSum
    .tap(scoreSum => Console.printLine(s"Score: ${scoreSum}"))

  val run = firstStar *> secondStar
