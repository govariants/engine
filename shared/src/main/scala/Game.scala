package org.govariants.engine

class Game(val size: Int, komi: Double) {

  var turn: Color = Black

  var legal_moves = LegalMoves()

  protected val board = new Board(size)

  def playable_actions(): List[String] = {
    val move_turn = turn match {
      case Black => "black_turn"
      case White => "white_turn"
    }
    List(move_turn, "black_resign", "white_resign")
  }

  def start(): List[String] = {
    legal_moves = board.legal_moves(turn)
    playable_actions()
  }

  def move_is_legal(intersection: Intersection): Boolean = {
    legal_moves.legal(intersection)
  }

  def play(intersection: Intersection): List[String] = {
    assert(legal_moves.legal.contains(intersection), "move is not playable")

    board.add_stone(intersection, turn)

    switch_turn()
    legal_moves = board.legal_moves(turn)

    playable_actions()
  }

  def compute_score(dead_stones: Iterable[Intersection]) = {
    board.score(komi, dead_stones)
  }

  def switch_turn() = {
    turn = turn.opposite
  }

  def display() = {
    println(toString())
    println()
  }

  override def toString(): String = board.toString()
}
