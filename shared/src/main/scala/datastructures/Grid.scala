package org.govariants.engine
package datastructures

abstract class Grid[T](val grid_size: Int, val initial_value: T) extends Iterable[(Intersection, T)] {

  protected val grid_builder: GridBuilder

  def get(x: Int, y: Int): T
  def get(intersection: Intersection): T

  def set(x: Int, y: Int, item: T): Unit
  def set(intersection: Intersection, item: T): Unit

  def iterator =
    for (i <- Iterator.range(0, grid_size); j <- Iterator.range(0, grid_size))
      yield (Intersection(i, j), get(i, j))

  def copy(): Grid[T] = {
    val grid_copy = grid_builder.build(grid_size, initial_value)
    iterator.foreach(t => grid_copy.set(t._1, t._2))
    grid_copy
  }

  def copy_from(grid: Grid[T]): Unit = {
    grid.iterator.foreach(t => set(t._1, t._2))
  }

  def same_grid(other_grid: Grid[T]): Boolean = {
    !zip(other_grid).exists(tt => tt._1._2 != tt._2._2)
  }
}

abstract class GridBuilder {
  def build[T](size: Int, initial_value: T): Grid[T]
}
