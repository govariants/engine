package org.govariants.engine

import fastparse._, MultiLineWhitespace._
import fastparse.Parsed.{ Success => FPSuccess }
import fastparse.Parsed.{ Failure => FPFailure }
import scala.annotation.tailrec
import scala.util.{ Failure, Success, Try }

case class SGFParserException(val message: String) extends Exception(message)

case class GameTree(sequence: IndexedSeq[Node], subtrees: IndexedSeq[GameTree]) {
  def main_line: IndexedSeq[Node] = {
    @tailrec
    def go(game_tree: GameTree, acc: Vector[Node]): Vector[Node] = {
      game_tree match {
        case GameTree(sequence, IndexedSeq()) => acc ++ sequence
        case GameTree(sequence, subtrees)     => go(subtrees(0), acc ++ sequence)
      }
    }
    go(this, Vector())
  }

  def main_line_to_board: Board = {
    val board = new Board(sequence(0).sz.getOrElse(Square(19)) match {
      case Square(size)               => size
      case Rectangular(columns, rows) => columns
    })

    for (node <- main_line) {
      if (node.w.isDefined) {
        node.w.get match {
          case Move(column, row) => board.add_stone(Intersection(column, row), White)
          case Pass              =>
        }

      }
      if (node.b.isDefined) {
        node.b.get match {
          case Move(column, row) => board.add_stone(Intersection(column, row), Black)
          case Pass              =>
        }
      }

    }
    board
  }

}

class Node(val properties: Map[String, Property]) {
  def sz = properties.get("SZ").asInstanceOf[Option[SZ]]
  def w = properties.get("W").asInstanceOf[Option[Turn]]
  def b = properties.get("B").asInstanceOf[Option[Turn]]
}

object Node {
  def apply(properties: Map[String, Property]): Node = new Node(properties)
  def unapply(node: Node): Map[String, Property] = node.properties
}

sealed trait Property

sealed trait RootProperty extends Property

sealed trait SZ extends RootProperty
case class Square(size: Int) extends SZ
case class Rectangular(columns: Int, rows: Int) extends SZ

sealed trait MoveProperty extends Property
sealed trait Turn extends MoveProperty
case class Move(column: Int, row: Int) extends Turn
object Pass extends Turn

case class StringProperty(string: String) extends Property

object SGFParser {
  object Parsers {
    def collection[_: P] = P(Start ~ gameTree.rep(1) ~ End).map(_.toVector)
    def gameTree[_: P]: P[GameTree] =
      P("(" ~ node.rep(1) ~ gameTree.rep ~ ")").map(t => GameTree(t._1.toVector, t._2.toVector))
    def node[_: P] = P(";" ~ property.rep).map(m => Node(m.toMap))
    def property[_: P]: P[(String, Property)] =
      P(
        (CharIn("A-Z").rep(1).! ~ "[").flatMap(s =>
          (s match {
            case "SZ" => size
            case "W"  => move
            case "B"  => move
            case _    => string
          }).map(p => (s, p))
        ) ~ "]"
      )
    def size[_: P] =
      P(CharsWhileIn("0-9").! ~ (":" ~ CharsWhileIn("0-9").!).?).map(c => {
        val columns = c._1.toInt
        val rows = c._2.map(_.toInt)
        if (rows.isDefined && rows.get != columns) Rectangular(columns, rows.get)
        else Square(columns)
      })

    def move[_: P] =
      P((CharIn("a-zA-Z").! ~ CharIn("a-zA-Z").!).?).map(o =>
        if (o.isDefined) Move(o.get._1.charAt(0) - 'a', o.get._2.charAt(0) - 'a')
        else Pass
      )

    def string[_: P] =
      P((("\\" ~ "]".!) | ("\\" ~ "\\".!) | (!"]" ~ AnyChar).!).rep(1)).map(cs => StringProperty(cs.mkString))
  }

  def parse_sgf_string(sgf_string: String): Try[Vector[GameTree]] =
    parse(sgf_string, Parsers.collection(_), verboseFailures = true) match {
      case FPSuccess(value, index)    => Success(value)
      case FPFailure(label, index, _) => Failure(SGFParserException(s"$label at index $index"))
    }
}
