package org.govariants.engine;
package datastructures;

import collection.mutable.ArrayBuffer

class ScalaGrid[T](grid_size: Int, initial_value: T) extends Grid[T](grid_size, initial_value) {

  protected val grid_builder = ScalaGridBuilder
  private val grid = new ArrayBuffer[ArrayBuffer[T]]()

  for (i <- 0 until grid_size) {
    val row = new ArrayBuffer[T]()
    row.padToInPlace(grid_size, initial_value)
    grid.append(row)
  }

  def get(x: Int, y: Int): T = grid(x)(y)
  def get(intersection: Intersection): T = grid(intersection.x)(intersection.y)

  def set(x: Int, y: Int, item: T): Unit = grid(x)(y) = item
  def set(intersection: Intersection, item: T): Unit =
    grid(intersection.x)(intersection.y) = item
}

object ScalaGridBuilder extends GridBuilder {
  def build[T](grid_size: Int, initial_value: T): ScalaGrid[T] = new ScalaGrid[T](grid_size, initial_value)
}
