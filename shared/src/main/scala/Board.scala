package org.govariants.engine

import scala.collection.mutable.ListBuffer

import scalajs.js.annotation.{ JSExportAll, JSExportTopLevel }
import org.govariants.engine.datastructures.GridBuilder
import org.govariants.engine.datastructures.Grid

@JSExportAll
@JSExportTopLevel("Board")
class Board(val size: Int)(implicit grid_builder: GridBuilder) {
  assert(size > 0, "size must be > 0")

  type Idx = Int

  val grid          = grid_builder.build[Option[Color]](size, None)
  val previous_grid = grid_builder.build[Option[Color]](size, None)

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
      val intersection = Intersection(i, j)
      if (groups.stone_liberties(intersection).size == 0) {
        if (move_would_capture(intersection, color) && !position_repeat(intersection, color)) {
          _legal_moves.append(intersection)
        }
      } else {
        _legal_moves.append(intersection)
      }
    }
    _legal_moves
  }

  def move_would_capture(intersection: Intersection, color: Color): Boolean = {
    groups.adjacent(intersection) exists (idx =>
      groups.color(idx) == color.opposite && groups.liberties_count(idx) == 1
    )
  }

  def position_repeat(intersection: Intersection, color: Color): Boolean = {
    val tmp_grid = grid.copy()
    add_stone_virtual(tmp_grid, intersection, color)
    tmp_grid.same_grid(previous_grid)
  }

  def add_stone_virtual(virtual_grid: Grid[Option[Color]], intersection: Intersection, color: Color) = {
    virtual_grid.set(intersection, Some(color))
    val neighbors: ListBuffer[Intersection] = get_neighbors(intersection).filter(is_stone)

    if (neighbors.length > 0) {
      val idx_processed: ListBuffer[Idx] = ListBuffer()
      for (neighbor <- neighbors) {
        val (idx, _color) = stone_idx_and_color(neighbor)
        if (!idx_processed.contains(idx) && _color != color && groups.liberties_count(idx) == 1) {
          for (stone <- groups.members(idx)) {
            virtual_grid.set(stone, None)
          }
        }
        idx_processed ++= ListBuffer(idx)
      }
    }
  }

  def add_stone(intersection: Intersection, color: Color) = {
    previous_grid.copy_from(grid)
    grid.set(intersection, Some(color))
    val neighbors: ListBuffer[Intersection] = get_neighbors(intersection).filter(is_stone)
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
              groups.add(intersection, idx)
              stone_idx = idx
            }
          } else {
            groups.liberties_count(idx) -= 1
            if (groups.liberties_count(idx) == 0) {
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
      groups.create(intersection, color)
    }
  }

  def is_stone(intersection: Intersection): Boolean = {
    grid.get(intersection).isDefined
  }

  def stone_idx_and_color(intersection: Intersection): (Idx, Color) = {
    val idx = groups.indexes.get(intersection)
    (idx, groups.color(idx))
  }

  def get_neighbors(intersection: Intersection): ListBuffer[Intersection] = {
    val neighbors: ListBuffer[Intersection] = ListBuffer()
    if (intersection.x > 0) {
      neighbors ++= ListBuffer(Intersection(intersection.x - 1, intersection.y))
    }
    if (intersection.x < size - 1) {
      neighbors ++= ListBuffer(Intersection(intersection.x + 1, intersection.y))
    }
    if (intersection.y > 0) {
      neighbors ++= ListBuffer(Intersection(intersection.x, intersection.y - 1))
    }
    if (intersection.y < size - 1) {
      neighbors ++= ListBuffer(Intersection(intersection.x, intersection.y + 1))
    }
    neighbors
  }

  def score() = {}
}
