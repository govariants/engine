package org.govariants.engine

import scala.collection.mutable.HashMap
import scala.collection.mutable.ListBuffer

import scalajs.js.annotation.{ JSExportAll, JSExportTopLevel }
import org.govariants.engine.datastructures.GridBuilder

@JSExportAll
@JSExportTopLevel("StoneGroups")
class StoneGroups(val size: Int, val board: Board)(implicit grid_builder: GridBuilder) {
  type Idx = Int

  var idx: Idx                                        = 1
  val members: HashMap[Idx, ListBuffer[Intersection]] = HashMap()
  val grid                                            = grid_builder.build[Idx](size, 0)
  val liberties: HashMap[Idx, Int]                    = HashMap()
  val color: HashMap[Idx, Color]                      = HashMap()

  def create_group(x: Int, y: Int, _color: Color) = {
    members.addOne((idx, ListBuffer(Intersection(x, y))))
    grid.set(x, y, idx)
    liberties.addOne((idx, stone_liberties(x, y).size))
    color.addOne((idx, _color))
    idx += 1
  }

  def add_to_group(x: Int, y: Int, idx: Idx) = {
    members(idx) ++= ListBuffer(Intersection(x, y))
    grid.set(x, y, idx)
    update_liberties(idx)
  }

  def merge(idx1: Idx, idx2: Idx) = {
    members(idx1) ++= members(idx2)
    for (intersection <- members(idx2)) {
      grid.set(intersection, idx1)
    }
    update_liberties(idx1)
  }

  def update_liberties(idx: Idx) = {
    var liberties_coord: Set[Intersection] = Set()
    for (intersection <- members(idx)) {
      liberties_coord ++= stone_liberties(intersection.x, intersection.y)
    }
    liberties(idx) = liberties_coord.size
  }

  def stone_liberties(x: Int, y: Int): Set[Intersection] = {
    board.get_neighbors(x, y).filterNot(board.stone).toSet
  }

  def adjacent_groups(x: Int, y: Int): Set[Idx] = {
    board
      .get_neighbors(x, y)
      .map(intersection => grid.get(intersection))
      .filter(idx => idx > 0 && idx != grid.get(x, y))
      .toSet
  }
}
