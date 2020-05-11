package org.govariants.engine

import scala.collection.mutable.HashMap
import scala.collection.mutable.ListBuffer

import scalajs.js.annotation.{ JSExportAll, JSExportTopLevel }

@JSExportAll
@JSExportTopLevel("StoneGroups")
class StoneGroups(val size: Int) {
  type Idx = Int

  var idx: Idx = 1
  var members: HashMap[Idx, ListBuffer[Intersection]] = HashMap()
  var grid = Array.ofDim[Idx](size, size)
  var liberties: HashMap[Idx, Int] = HashMap()
  var color: HashMap[Idx, Color] = HashMap()

  for (i <- 0 until size) {
    for (j <- 0 until size) {
      grid(i)(j) = 0
    }
  }

  def create_group(x: Int, y: Int, _color: Color) = {
    members.addOne((idx, ListBuffer(Intersection(x, y))))
    grid(x)(y) = idx
    liberties.addOne((idx, stone_liberties(x, y).size))
    color.addOne((idx, _color))
    idx += 1
  }

  def add_to_group(x: Int, y: Int, idx: Idx) = {
    members(idx) ++= ListBuffer(Intersection(x, y))
    grid(x)(y) = idx
    update_liberties(idx)
  }

  def merge(idx1: Idx, idx2: Idx) = {
    members(idx1) ++= members(idx2)
    for (intersection <- members(idx2)) {
      grid(intersection.x)(intersection.y) = idx1
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
    var liberties_coord: Set[Intersection] = Set()
    if (x > 0 && grid(x - 1)(y) == 0) {
      liberties_coord ++= Set(Intersection(x - 1, y))
    }
    if (x < size - 1 && grid(x + 1)(y) == 0) {
      liberties_coord ++= Set(Intersection(x + 1, y))
    }
    if (y > 0 && grid(x)(y - 1) == 0) {
      liberties_coord ++= Set(Intersection(x, y - 1))
    }
    if (y < size - 1 && grid(x)(y + 1) == 0) {
      liberties_coord ++= Set(Intersection(x, y + 1))
    }
    liberties_coord
  }

  def adjacent_groups(x: Int, y: Int): Set[Idx] = {
    var _adjacent_groups: Set[Idx] = Set()
    val current_group = grid(x)(y)
    if (x > 0 && grid(x - 1)(y) > 0) {
      _adjacent_groups ++= Set(grid(x - 1)(y))
    }
    if (x < size - 1 && grid(x + 1)(y) > 0) {
      _adjacent_groups ++= Set(grid(x + 1)(y))
    }
    if (y > 0 && grid(x)(y - 1) > 0) {
      _adjacent_groups ++= Set(grid(x)(y - 1))
    }
    if (y < size - 1 && grid(x)(y + 1) > 0) {
      _adjacent_groups ++= Set(grid(x)(y + 1))
    }
    _adjacent_groups - current_group
  }
}
