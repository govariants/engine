package org.govariants.engine

import collection.mutable
import util.Random

import datastructures.Grid

class ZobristHashes(val size: Int) {
  val random_generator = new Random
  val zobrist_table = Array.ofDim[Long](size * size, 2)
  var hashes = new mutable.ListBuffer[Long]

  for (position <- 0 until size * size; color <- 0 until 2) {
    zobrist_table(position)(color) = random_generator.nextLong
  }

  def compute_hash(grid: Grid[Option[Color]]): Long = {
    var hash = 0L
    for (i <- 0 until size; j <- 0 until size) {
      grid(i, j) match {
        case Some(Black) => hash ^= zobrist_table(j * size + i)(0)
        case Some(White) => hash ^= zobrist_table(j * size + i)(1)
        case None        =>
      }
    }
    hash
  }

  def add_position(grid: Grid[Option[Color]]) = {
    hashes += compute_hash(grid)
  }

  def position_repeat(grid: Grid[Option[Color]]): Boolean = {
    hashes.contains(compute_hash(grid))
  }
}
