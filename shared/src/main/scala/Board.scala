package org.govariants.engine

import collection.mutable

import org.govariants.engine.datastructures.Grid

class Board(val size: Int) {
  assert(size > 0, "size must be > 0")

  val grid = Grid[Option[Color]](size, None)
  val zobrist_hashes = new ZobristHashes(size)

  val groups = new StoneGroups(size, this)

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
        grid(i, j) match {
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

  def legal_moves(color: Color): LegalMoves = {
    def intersection_playability(intersection: Intersection): PlayabilityType =
      if (position_repeat(intersection, color))
        Ko
      else if (groups.stone_liberties(intersection).size > 0 || move_would_capture(intersection, color))
        Legal
      else
        Illegal

    grid
      .filter(_._2.isEmpty)
      .map(_._1)
      .foldLeft(LegalMoves())((legal_moves, intersection) =>
        intersection_playability(intersection) match {
          case Ko      => LegalMoves(legal_moves.legal, legal_moves.ko + intersection)
          case Legal   => LegalMoves(legal_moves.legal + intersection, legal_moves.ko)
          case Illegal => legal_moves
        }
      )
  }

  def move_would_capture(intersection: Intersection, color: Color): Boolean = {
    groups.adjacent(intersection) exists (idx =>
      groups.color(idx) == color.opposite && groups.liberties_count(idx) == 1
    )
  }

  def position_repeat(intersection: Intersection, color: Color): Boolean = {
    val tmp_grid = grid.copy()
    add_stone_virtual(tmp_grid, intersection, color)
    zobrist_hashes.position_repeat(tmp_grid)
  }

  def add_stone_virtual(virtual_grid: Grid[Option[Color]], intersection: Intersection, color: Color): Unit = {
    virtual_grid(intersection) = Some(color)
    val neighbors = get_neighbors(intersection).filter(is_stone)

    if (neighbors.length > 0) {
      val idx_processed = mutable.ListBuffer[Idx]()
      for (neighbor <- neighbors) {
        val (idx, _color) = stone_idx_and_color(neighbor)
        if (!idx_processed.contains(idx) && _color != color && groups.liberties_count(idx) == 1) {
          for (stone <- groups.members(idx)) {
            virtual_grid(stone) = None
          }
        }
        idx_processed += idx
      }
    }
  }

  def add_stone(intersection: Intersection, color: Color): Unit = {
    grid(intersection) = Some(color)
    val neighbors = get_neighbors(intersection).filter(is_stone)
    var stone_idx: Idx = 0

    if (neighbors.length > 0) {
      val idx_processed = mutable.ListBuffer[Idx]()
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
                grid(stone) = None
              }
              groups.remove(idx)
            }
          }
        }
        idx_processed += idx
      }
    }
    if (stone_idx == 0) {
      groups.create(intersection, color)
    }
    zobrist_hashes.add_position(grid)
  }

  def is_stone(intersection: Intersection): Boolean = {
    grid(intersection).isDefined
  }

  def stone_idx_and_color(intersection: Intersection): (Idx, Color) = {
    val idx = groups.indexes(intersection)
    (idx, groups.color(idx))
  }

  def get_neighbors(intersection: Intersection): mutable.ListBuffer[Intersection] = {
    val neighbors = mutable.ListBuffer[Intersection]()
    if (intersection.x > 0) {
      neighbors += Intersection(intersection.x - 1, intersection.y)
    }
    if (intersection.x < size - 1) {
      neighbors += Intersection(intersection.x + 1, intersection.y)
    }
    if (intersection.y > 0) {
      neighbors += Intersection(intersection.x, intersection.y - 1)
    }
    if (intersection.y < size - 1) {
      neighbors += Intersection(intersection.x, intersection.y + 1)
    }
    neighbors
  }

  def compute_territories(
      dead_stones: Iterable[Intersection]
  ): (mutable.ListBuffer[Intersection], mutable.ListBuffer[Intersection]) = {
    val grid_without_dead_stone = this.grid.copy()
    val visited = Grid[Boolean](size, false)
    val black_territory = new mutable.ListBuffer[Intersection]
    val white_territory = new mutable.ListBuffer[Intersection]
    var black_border = false
    var white_border = false

    for (dead_stone <- dead_stones) {
      val dead_stone_idx = groups.indexes(dead_stone)
      for (stone <- groups.members(dead_stone_idx)) {
        grid_without_dead_stone(stone) = None
      }
    }

    for (i <- 0 until size; j <- 0 until size if !visited(i, j)) {
      val intersection = Intersection(i, j)
      visited(intersection) = true
      black_border = false
      white_border = false
      grid_without_dead_stone(i, j) match {
        case Some(Black) => black_territory += intersection
        case Some(White) => white_territory += intersection
        case None => {
          val territory = new mutable.ListBuffer[Intersection]
          recursive_compute_territories(intersection, territory)
          if (black_border && !white_border) black_territory ++= territory
          if (white_border && !black_border) white_territory ++= territory
        }
      }
    }

    def recursive_compute_territories(
        intersection: Intersection,
        territory: mutable.ListBuffer[Intersection]
    ): Unit = {
      visited(intersection) = true
      territory += intersection
      for (neighbor <- get_neighbors(intersection)) {
        if (grid_without_dead_stone(neighbor).isDefined) {
          grid(neighbor) match {
            case Some(Black) => black_border = true
            case Some(White) => white_border = true
            case None        =>
          }
        } else if (!visited(neighbor)) {
          recursive_compute_territories(neighbor, territory)
        }
      }
    }

    (black_territory, white_territory)
  }

  def score(komi: Double, dead_stones: Iterable[Intersection]): (Double, Double) = {
    val (black_territory, white_territory) = compute_territories(dead_stones)
    (black_territory.size, white_territory.size + komi)
  }
}
