package org.govariants.engine

import scala.collection.mutable.ListBuffer

import scalajs.js.annotation.{ JSExportAll, JSExportTopLevel }

@JSExportAll
@JSExportTopLevel("Board")
class Board(val size: Int) {
  assert(size > 0, "size must be > 0")

  type Idx = Int

  var grid                = Array.ofDim[Option[Color]](size, size)
  val groups: StoneGroups = new StoneGroups(size, this)

  for (i <- 0 until size) {
    for (j <- 0 until size) {
      grid(i)(j) = None
    }
  }

  override def toString(): String = {
    val string = new StringBuilder("  ")
    for (i <- 0 until size) {
      string += ('a' + i).asInstanceOf[Char]
      string += ' '
    }
    string += '\n'
    for (j <- 0 until size) {
      string ++= (j + 1).toString
      string += ' '
      for (i <- 0 until size) {
        grid(i)(j) match {
          case Some(Black) => string += 'X'
          case Some(White) => string += 'O'
          case None        => string += '+'
        }
        if (i < size - 1) string += ' '
      }
      string += '\n'
    }
    string.toString
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
    val neighbors: ListBuffer[Intersection] = get_neighbors(x, y).filter(stone)
    var stone_idx: Idx                      = 0

    if (neighbors.length > 0) {
      var idx_processed: ListBuffer[Idx] = ListBuffer()
      for (neighbor <- neighbors) {
        val (idx, _color) = stone_idx_and_color(neighbor)
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

  def stone(intersection: Intersection): Boolean = {
    grid(intersection.x)(intersection.y) != None
  }

  def stone_idx_and_color(intersection: Intersection): (Idx, Color) = {
    val idx = groups.grid(intersection.x)(intersection.y)
    (idx, groups.color(idx))
  }

  def get_neighbors(x: Int, y: Int): ListBuffer[Intersection] = {
    val neighbors: ListBuffer[Intersection] = ListBuffer()
    if (x > 0) {
      neighbors ++= ListBuffer(Intersection(x - 1, y))
    }
    if (x < size - 1) {
      neighbors ++= ListBuffer(Intersection(x + 1, y))
    }
    if (y > 0) {
      neighbors ++= ListBuffer(Intersection(x, y - 1))
    }
    if (y < size - 1) {
      neighbors ++= ListBuffer(Intersection(x, y + 1))
    }
    neighbors
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

  def compute_groups_from_scratch() = {
    var visited: Set[Intersection] = Set()
    for (i <- 0 until size; j <- 0 until size if grid(i)(j) != None && groups.grid(i)(j) == 0) {
      search_connected_stones_recursive(i, j, grid(i)(j).get, 0, visited)
    }

    def search_connected_stones_recursive(
        x: Int,
        y: Int,
        color: Color,
        _idx: Idx,
        _visited: Set[Intersection]
    ) {
      var visited = _visited + Intersection(x, y)
      var idx     = _idx
      if (idx == 0) {
        groups.create_group(x, y, color)
        idx = groups.grid(x)(y)
      } else {
        groups.add_to_group(x, y, idx)
      }
      for (
        neighbor <- get_neighbors(x, y)
          .filter(stone)
          .filter(stone => same_color(stone, color))
          .filterNot(visited)
      ) {
        search_connected_stones_recursive(neighbor.x, neighbor.y, color, idx, visited)
      }
    }

    def same_color(intersection: Intersection, color: Color): Boolean = {
      grid(intersection.x)(intersection.y).get == color
    }
  }

  def score() = {}
}

@JSExportAll
@JSExportTopLevel("Intersection")
case class Intersection(val x: Int, val y: Int)
