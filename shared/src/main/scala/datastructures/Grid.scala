package org.govariants.engine
package datastructures

abstract class Grid(size: Int) {
  def get(x: Int, y: Int): Option[Color]
  def get(intersection: Intersection): Option[Color]

  def set(x: Int, y: Int, color: Option[Color]): Unit
  def set(intersection: Intersection, color: Option[Color]): Unit
}

abstract class GridBuilder {
  def build(size: Int): Grid
}
