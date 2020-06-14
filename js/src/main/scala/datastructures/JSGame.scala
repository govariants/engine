package org.govariants.engine;

import scalajs.js.{ Array => JSArray, Iterable => JSIterable, Tuple2 => JSTuple2 }
import scalajs.js.annotation.{ JSExport, JSExportTopLevel }
import scalajs.js.JSConverters._

@JSExportTopLevel("Game")
class JSGame(size: Int, komi: Double) extends Game(size, komi) {

  @JSExport("turn")
  def jsturn: String =
    turn match {
      case White => "white"
      case Black => "black"
    }

  @JSExport("legal_moves")
  def jslegal_moves: JSTuple2[JSArray[JSTuple2[Int, Int]], JSArray[JSTuple2[Int, Int]]] = {
    (
      legal_moves.legal.map(i => ((i.x, i.y): JSTuple2[Int, Int])).toJSArray,
      legal_moves.ko.map(i => ((i.x, i.y): JSTuple2[Int, Int])).toJSArray
    )
  }

  @JSExport("start")
  def jsstart(): JSArray[String] = super.start().toJSArray

  @JSExport("move_is_legal")
  def jsmove_is_legal(column: Int, row: Int): Boolean =
    super.move_is_legal(Intersection(column, row))

  @JSExport("play")
  def jsplay(column: Int, row: Int): JSArray[String] =
    super.play(Intersection(column, row)).toJSArray

  @JSExport("compute_score")
  def jscompute_score(dead_stones: JSIterable[(Int, Int)]): JSTuple2[Double, Double] = {
    board.score(komi, dead_stones.map(t => Intersection(t._1, t._2)))
  }

  @JSExport("switch_turn")
  def jsswitch_turn() = super.switch_turn()

  @JSExport("display")
  def jsdisplay() = super.display()
}
