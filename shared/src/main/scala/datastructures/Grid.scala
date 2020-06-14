package org.govariants.engine
package datastructures

import collection.mutable

class Grid[T](val grid_size: Int, val initial_value: T) extends Iterable[(Intersection, T)] {

  private val grid = new mutable.ArrayBuffer[mutable.ArrayBuffer[T]]()

  for (i <- 0 until grid_size) {
    val row = new mutable.ArrayBuffer[T]()
    row.padToInPlace(grid_size, initial_value)
    grid.append(row)
  }

  def apply(x: Int, y: Int): T = grid(x)(y)
  def apply(intersection: Intersection): T = grid(intersection.x)(intersection.y)

  def update(x: Int, y: Int, item: T): Unit = grid(x)(y) = item
  def update(intersection: Intersection, item: T): Unit = grid(intersection.x)(intersection.y) = item

  def iterator =
    for (i <- Iterator.range(0, grid_size); j <- Iterator.range(0, grid_size))
      yield (Intersection(i, j), this(i, j))

  def copy(): Grid[T] = {
    val grid_copy = Grid(grid_size, initial_value)
    foreach(t => grid_copy(t._1) = t._2)
    grid_copy
  }

  def copy_from(grid: Grid[T]): Unit = {
    grid.iterator.foreach(t => this(t._1) = t._2)
  }

  def same_grid(other_grid: Grid[T]): Boolean = {
    zip(other_grid).forall(tt => tt._1._2 == tt._2._2)
  }
}

object Grid {
  def apply[T](grid_size: Int, initial_value: T): Grid[T] = new Grid(grid_size, initial_value)
}
