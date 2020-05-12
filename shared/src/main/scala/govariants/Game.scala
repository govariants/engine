package org.govariants.engine

import scala.collection.mutable.ListBuffer

import scalajs.js.annotation.{ JSExportAll, JSExportTopLevel }

@JSExportAll
@JSExportTopLevel("Game")
class Game(val size: Int) {

  var turn: Color = Black

  var legal_moves: ListBuffer[Intersection] = ListBuffer()

  val board = new Board(size)

  def compute_playable_action(): List[String] = {
    val move_turn = turn match {
      case Black => "black_turn"
      case White => "white_turn"
    }
    List(move_turn, "black_resign", "white_resign")
  }

  def start(): List[String] = {
    legal_moves = board.legal_moves(turn)
    compute_playable_action()
  }

  def move_is_legal(x: Int, y: Int): Boolean = {
    legal_moves.contains(Intersection(x, y))
  }

  def play(x: Int, y: Int): List[String] = {
    assert(legal_moves.contains(Intersection(x, y)), "move is not playable")

    board.add_stone(x, y, turn)

    switch_turn()
    legal_moves = board.legal_moves(turn)

    compute_playable_action()
  }

  def compute_score() = {
    board.score()
  }

  def switch_turn() = {
    turn = turn.opposite
  }

  def display() = {
    board.display()
    println()
  }
}
