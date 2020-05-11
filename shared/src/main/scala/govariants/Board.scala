package org.govariants.engine

import scala.collection.mutable.ListBuffer

import scalajs.js.annotation.{ JSExportAll, JSExportTopLevel }

@JSExportAll
@JSExportTopLevel("Board")
class Board(val size: Int) {
  assert(size > 0, "size must be > 0")

  type Idx = Int

  var grid = Array.ofDim[Option[Color]](size, size)
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
          case None => print("+ ")
        }
      }
      println()
    }
  }

  def legal_moves(): ListBuffer[Intersection] = {
    var _legal_moves: ListBuffer[Intersection] = ListBuffer()
    for (i <- 0 until size) {
      for (j <- 0 until size) {
        if (grid(i)(j) == None) {
          _legal_moves.append(Intersection(i, j))
        }
      }
    }
    _legal_moves
  }

  def add_stone(x: Int, y: Int, color: Color) = {
    grid(x)(y) = Some(color)
    val neighbors: ListBuffer[(Intersection, Idx, Color)] = get_neighbors(x, y)
    var stone_idx: Idx = 0

    if (neighbors.length > 0) {
      var recompute_liberties = false
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
      grid(x - 1)(y) match {
        case Some(color) => neighbors ++= 
          ListBuffer((Intersection(x - 1, y), groups.grid(x - 1)(y), color))
        case None =>
      }
    }
    if (x < size - 1) {
      grid(x + 1)(y) match {
        case Some(color) => neighbors ++= 
          ListBuffer((Intersection(x + 1, y), groups.grid(x + 1)(y), color))
        case None =>
      }
    }
    if (y > 0) {
      grid(x)(y - 1) match {
        case Some(color) => neighbors ++= 
          ListBuffer((Intersection(x, y - 1), groups.grid(x)(y - 1), color))
        case None =>
      }
    }
    if (y < size - 1) {
      grid(x)(y + 1) match {
        case Some(color) => neighbors ++= 
          ListBuffer((Intersection(x, y + 1), groups.grid(x)(y + 1), color))
        case None =>
      }
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

  def score() = {

  }
}

@JSExportAll
@JSExportTopLevel("Intersection")
case class Intersection(val x: Int, val y: Int)
