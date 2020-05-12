package org.govariants.engine

import scala.collection.mutable.ListBuffer

import scalajs.js.annotation.{ JSExportAll, JSExportTopLevel }

@JSExportAll
@JSExportTopLevel("Board")
class Board(val size: Int) {
  assert(size > 0, "size must be > 0")

  type Idx = Int

  var grid                = Array.ofDim[Option[Color]](size, size)
  val groups: StoneGroups = new StoneGroups(size)

  for (i <- 0 until size) {
    for (j <- 0 until size) {
      grid(i)(j) = None
    }
  }

  def display() = {
    print("  ")
    for (i <- 0 until size) {
      print(('a' + i).asInstanceOf[Char])
      print(" ")
    }
    println()
    for (j <- 0 until size) {
      print(j + 1)
      print(" ")
      for (i <- 0 until size) {
        grid(i)(j) match {
          case Some(Black) => print("X ")
          case Some(White) => print("O ")
          case None        => print("+ ")
        }
      }
      println()
    }
  }

  def legal_moves(color: Color): ListBuffer[Intersection] = {
    var _legal_moves: ListBuffer[Intersection] = ListBuffer()
    for (i <- 0 until size; j <- 0 until size if grid(i)(j) == None) {
      if (groups.stone_liberties(i, j).size == 0) {
        if (move_would_capture(i, j, color)) {
          _legal_moves.append(Intersection(i, j))
        }
      } else {
        _legal_moves.append(Intersection(i, j))
      }
    }
    _legal_moves
  }

  def move_would_capture(x: Int, y: Int, color: Color): Boolean = {
    for (adjacent_group <- groups.adjacent_groups(x, y)) {
      if (groups.color(adjacent_group) == color.opposite && groups.liberties(adjacent_group) == 1) {
        return true
      }
    }
    false
  }

  def add_stone(x: Int, y: Int, color: Color) = {
    grid(x)(y) = Some(color)
    val neighbors: ListBuffer[(Intersection, Idx, Color)] = get_neighbors(x, y)
    var stone_idx: Idx                                    = 0

    if (neighbors.length > 0) {
      var recompute_liberties            = false
      var idx_processed: ListBuffer[Idx] = ListBuffer()
      for (neighbor <- neighbors) {
        val (intersection, idx, _color) = neighbor
        if (!idx_processed.contains(idx)) {
          if (_color == color) {
            if (stone_idx > 0) {
              groups.merge(stone_idx, idx)
            } else {
              groups.add_to_group(x, y, idx)
              stone_idx = idx
            }
          } else {
            groups.liberties(idx) -= 1
            if (groups.liberties(idx) == 0) {
              remove_group(idx)
            }
          }
        }
        idx_processed ++= ListBuffer(idx)
      }
    }
    if (stone_idx == 0) {
      groups.create_group(x, y, color)
    }
  }

  def get_neighbors(x: Int, y: Int): ListBuffer[(Intersection, Idx, Color)] = {
    var neighbors: ListBuffer[(Intersection, Idx, Color)] = ListBuffer()
    if (x > 0) {
      add_neighbor(x - 1, y, neighbors)
    }
    if (x < size - 1) {
      add_neighbor(x + 1, y, neighbors)
    }
    if (y > 0) {
      add_neighbor(x, y - 1, neighbors)
    }
    if (y < size - 1) {
      add_neighbor(x, y + 1, neighbors)
    }
    neighbors
  }

  def add_neighbor(x: Int, y: Int, neighbors: ListBuffer[(Intersection, Idx, Color)]) = {
    grid(x)(y) match {
      case Some(color) =>
        neighbors ++=
          ListBuffer((Intersection(x, y), groups.grid(x)(y), color))
      case None =>
    }
  }

  def remove_group(idx: Idx) = {
    var adjacent_groups: Set[Idx] = Set()
    for (intersection <- groups.members(idx)) {
      grid(intersection.x)(intersection.y) = None
      groups.grid(intersection.x)(intersection.y) = 0
      adjacent_groups ++= groups.adjacent_groups(intersection.x, intersection.y)
    }
    for (group <- adjacent_groups) {
      groups.update_liberties(group)
    }
  }

  def score() = {}
}

@JSExportAll
@JSExportTopLevel("Intersection")
case class Intersection(val x: Int, val y: Int)
