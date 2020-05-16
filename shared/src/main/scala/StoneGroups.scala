package org.govariants.engine

import scala.collection.mutable.HashMap
import scala.collection.mutable.ListBuffer

import scalajs.js.annotation.{ JSExportAll, JSExportTopLevel }
import org.govariants.engine.datastructures.GridBuilder

@JSExportAll
@JSExportTopLevel("StoneGroups")
class StoneGroups(val size: Int, val board: Board)(implicit grid_builder: GridBuilder) {
  type Idx = Int

  var idx_counter: Idx                                = 1
  val members: HashMap[Idx, ListBuffer[Intersection]] = HashMap()
  val grid                                            = grid_builder.build[Idx](size, 0)
  val liberties: HashMap[Idx, Int]                    = HashMap()
  val color: HashMap[Idx, Color]                      = HashMap()

  def create(x: Int, y: Int, color: Color) = {
    members += ((idx_counter, ListBuffer(Intersection(x, y))))
    grid.set(x, y, idx_counter)
    liberties += ((idx_counter, stone_liberties(x, y).size))
    this.color += ((idx_counter, color))
    idx_counter += 1
  }

  def liberty_not_counted_in(idx: Idx, liberty: Intersection): Boolean = {
    !board
      .get_neighbors(liberty.x, liberty.y)
      .filter(board.stone)
      .exists(neighbor => grid.get(neighbor) == idx)
  }

  def add(x: Int, y: Int, idx: Idx) = {
    members(idx) ++= ListBuffer(Intersection(x, y))
    liberties(idx) -= 1
    for (liberty <- stone_liberties(x, y)) {
      if (liberty_not_counted_in(idx, liberty)) {
        liberties(idx) += 1
      }
    }
    grid.set(x, y, idx)
  }

  def merge(idx1: Idx, idx2: Idx) = {
    members(idx1) ++= members(idx2)
    for (intersection <- members(idx2)) {
      for (liberty <- stone_liberties(intersection.x, intersection.y)) {
        if (liberty_not_counted_in(idx1, liberty)) {
          liberties(idx1) += 1
        }
      }
      grid.set(intersection, idx1)
    }
  }

  def stone_liberties(x: Int, y: Int): Set[Intersection] = {
    board.get_neighbors(x, y).filterNot(board.stone).toSet
  }

  def adjacent(x: Int, y: Int): Set[Idx] = {
    board
      .get_neighbors(x, y)
      .map(intersection => grid.get(intersection))
      .filter(idx => idx > 0 && idx != grid.get(x, y))
      .toSet
  }

  def remove(idx: Idx) = {
    for (stone <- members(idx)) {
      board
        .get_neighbors(stone.x, stone.y)
        .map(intersection => grid.get(intersection))
        .filter(neighbor_idx => neighbor_idx > 0 && neighbor_idx != idx)
        .foreach(neighbor_idx => liberties(neighbor_idx) += 1)
      grid.set(stone, 0)
    }
  }
}
