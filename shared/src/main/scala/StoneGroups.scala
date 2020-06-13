package org.govariants.engine

import scala.collection.mutable.HashMap
import scala.collection.mutable.ListBuffer

import scalajs.js.annotation.{ JSExportAll, JSExportTopLevel }
import org.govariants.engine.datastructures.GridBuilder

@JSExportAll
@JSExportTopLevel("StoneGroups")
class StoneGroups(val size: Int, val board: Board)(implicit grid_builder: GridBuilder) {

  var idx_counter: Idx = 1
  val members = HashMap[Idx, ListBuffer[Intersection]]()
  val indexes = grid_builder.build[Idx](size, 0)
  val liberties_count = HashMap[Idx, Int]()
  val color = HashMap[Idx, Color]()

  def create(intersection: Intersection, color: Color) = {
    members(idx_counter) = ListBuffer(intersection)
    indexes.set(intersection, idx_counter)
    liberties_count(idx_counter) = stone_liberties(intersection).size
    this.color(idx_counter) = color
    idx_counter += 1
  }

  def liberty_not_counted_in(idx: Idx, liberty: Intersection): Boolean = {
    !board.get_neighbors(liberty).filter(board.is_stone).exists(neighbor => indexes.get(neighbor) == idx)
  }

  def add(intersection: Intersection, idx: Idx) = {
    members(idx) += intersection
    liberties_count(idx) -= 1
    for (liberty <- stone_liberties(intersection)) {
      if (liberty_not_counted_in(idx, liberty)) {
        liberties_count(idx) += 1
      }
    }
    indexes.set(intersection, idx)
  }

  def merge(idx1: Idx, idx2: Idx) = {
    members(idx1) ++= members(idx2)
    for (stone <- members(idx2)) {
      for (liberty <- stone_liberties(stone)) {
        if (liberty_not_counted_in(idx1, liberty)) {
          liberties_count(idx1) += 1
        }
      }
      indexes.set(stone, idx1)
    }
  }

  def stone_liberties(stone: Intersection): Set[Intersection] = {
    board.get_neighbors(stone).filterNot(board.is_stone).toSet
  }

  def adjacent(intersection: Intersection): Set[Idx] = {
    board
      .get_neighbors(intersection)
      .map(intersection => indexes.get(intersection))
      .filter(idx => idx > 0 && idx != indexes.get(intersection))
      .toSet
  }

  def remove(idx: Idx) = {
    for (stone <- members(idx)) {
      board
        .get_neighbors(stone)
        .map(intersection => indexes.get(intersection))
        .filter(neighbor_idx => neighbor_idx > 0 && neighbor_idx != idx)
        .foreach(neighbor_idx => liberties_count(neighbor_idx) += 1)
      indexes.set(stone, 0)
    }
  }
}
