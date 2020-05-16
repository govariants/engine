package org.govariants.engine

import scala.collection.mutable.ListBuffer

import scalajs.js.annotation.{ JSExportAll, JSExportTopLevel }
import org.govariants.engine.datastructures.GridBuilder

@JSExportAll
@JSExportTopLevel("Board")
class Board(val size: Int)(implicit grid_builder: GridBuilder) {
  assert(size > 0, "size must be > 0")

  type Idx = Int

  val grid = grid_builder.build[Option[Color]](size, None)

  val groups: StoneGroups = new StoneGroups(size, this)

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
        grid.get(i, j) match {
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
    val _legal_moves: ListBuffer[Intersection] = ListBuffer()
    for (i <- 0 until size; j <- 0 until size if grid.get(i, j) == None) {
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
    groups.adjacent(x, y) exists (idx => groups.color(idx) == color.opposite && groups.liberties(idx) == 1)
  }

  def add_stone(x: Int, y: Int, color: Color) = {
    grid.set(x, y, Some(color))
    val neighbors: ListBuffer[Intersection] = get_neighbors(x, y).filter(stone)
    var stone_idx: Idx                      = 0

    if (neighbors.length > 0) {
      val idx_processed: ListBuffer[Idx] = ListBuffer()
      for (neighbor <- neighbors) {
        val (idx, _color) = stone_idx_and_color(neighbor)
        if (!idx_processed.contains(idx)) {
          if (_color == color) {
            if (stone_idx > 0) {
              groups.merge(stone_idx, idx)
            } else {
              groups.add(x, y, idx)
              stone_idx = idx
            }
          } else {
            groups.liberties(idx) -= 1
            if (groups.liberties(idx) == 0) {
              for (stone <- groups.members(idx)) {
                grid.set(stone, None)
              }
              groups.remove(idx)
            }
          }
        }
        idx_processed ++= ListBuffer(idx)
      }
    }
    if (stone_idx == 0) {
      groups.create(x, y, color)
    }
  }

  def stone(intersection: Intersection): Boolean = {
    grid.get(intersection).isDefined
  }

  def stone_idx_and_color(intersection: Intersection): (Idx, Color) = {
    val idx = groups.grid.get(intersection)
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

  def score() = {}
}
