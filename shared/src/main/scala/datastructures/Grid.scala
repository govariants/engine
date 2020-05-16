package org.govariants.engine
package datastructures

abstract class Grid[T](size: Int, initial_value: T) {
  def get(x: Int, y: Int): T
  def get(intersection: Intersection): T

  def set(x: Int, y: Int, item: T): Unit
  def set(intersection: Intersection, item: T): Unit
}

abstract class GridBuilder {
  def build[T](size: Int, initial_value: T): Grid[T]
}
