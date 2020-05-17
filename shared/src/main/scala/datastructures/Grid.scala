package org.govariants.engine
package datastructures

abstract class Grid[T](val size: Int, val initial_value: T) {

  protected val grid_builder: GridBuilder

  def get(x: Int, y: Int): T
  def get(intersection: Intersection): T

  def set(x: Int, y: Int, item: T): Unit
  def set(intersection: Intersection, item: T): Unit

  def copy(): Grid[T] = {
    val grid_copy = grid_builder.build(size, initial_value)
    for (i <- 0 until size; j <- 0 until size) {
      grid_copy.set(i, j, get(i, j))
    }
    grid_copy
  }

  def copy_from(grid: Grid[T]): Unit = {
    for (i <- 0 until size; j <- 0 until size) {
      set(i, j, grid.get(i, j))
    }
  }

  def same_grid(other_grid: Grid[T]): Boolean = {
    var result = true
    for (i <- 0 until size; j <- 0 until size) {
      result &&= get(i, j) == other_grid.get(i, j)
    }
    result
  }
}

abstract class GridBuilder {
  def build[T](size: Int, initial_value: T): Grid[T]
}
