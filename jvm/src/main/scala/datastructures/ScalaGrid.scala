package org.govariants.engine;
package datastructures;

import collection.mutable.ArrayBuffer

class ScalaGrid[T](size: Int, initial_value: T) extends Grid[T](size, initial_value) {
  private val grid = new ArrayBuffer[ArrayBuffer[T]]()
  for (i <- 0 until size) {
    val row = new ArrayBuffer[T]()
    row.padToInPlace(size, initial_value)
    grid.append(row)
  }

  def get(x: Int, y: Int): T             = grid(x)(y)
  def get(intersection: Intersection): T = grid(intersection.x)(intersection.y)

  def set(x: Int, y: Int, item: T): Unit = grid(x)(y) = item
  def set(intersection: Intersection, item: T): Unit =
    grid(intersection.x)(intersection.y) = item

  def copy(): ScalaGrid[T] = {
    val copied_grid = ScalaGridBuilder.build[T](size, initial_value)
    for (i <- 0 until size; j <- 0 until size) {
      copied_grid.set(i, j, grid(i)(j))
    }
    copied_grid
  }

  def copy_from(grid: Grid[T]): Unit = {
    for (i <- 0 until size; j <- 0 until size) {
      this.grid(i)(j) = grid.get(i, j)
    }
  }

  override def equals(that: Any): Boolean = {
    that match {
      case grid: Grid[T] => {
        var grid_are_equal = true
        for (i <- 0 until size; j <- 0 until size) {
          grid_are_equal = grid_are_equal && (this.grid(i)(j) == grid.get(i, j))
        }
        grid_are_equal
      }
      case _ => false
    }
  }
}
