package govariants

import scala.collection.mutable.ListBuffer

import scalajs.js.annotation.{ JSExportAll, JSExportTopLevel }

import govariants.Intersection
import govariants.Board

@JSExportAll
@JSExportTopLevel("Game")
class Game(val size: Int) {

  val EMPTY = 0
  val BLACK = 1
  val WHITE = 2

  var turn = BLACK

  var legal_moves: ListBuffer[Intersection] = ListBuffer()

  var board = new Board(size)

  def compute_legal_moves() = {
    legal_moves = ListBuffer()
    for (i <- 0 until size) {
      for (j <- 0 until size) {
        if (board.grid(i)(j) == EMPTY) {
          legal_moves.append(Intersection(i, j))
        }
      }
    }
  }

  def compute_playable_action(): List[String] = {
    val move_turn = turn match {
      case BLACK => "black_turn"
      case WHITE => "white_turn"
    }
    List(move_turn, "black_resign", "white_resign")
  }

  def start(): List[String] = {
    compute_legal_moves()
    compute_playable_action()
  }

  def play(x: Int, y: Int): List[String] = {
    assert(legal_moves.contains(Intersection(x, y)), "move is not playable")

    board.grid(x)(y) = turn

    compute_legal_moves()

    switch_turn()
    compute_playable_action()
  }

  def switch_turn() = {
    assert(turn == BLACK || turn == WHITE, "turn is neither black or white")

    turn = turn match {
      case BLACK => WHITE
      case WHITE => BLACK
    }
  }

  def display() = {
    board.display()
    println()
  }
}
