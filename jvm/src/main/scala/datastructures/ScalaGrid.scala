package org.govariants.engine;
package datastructures;

class ScalaGrid(size: Int) extends Grid(size) {
  private val grid = Array.ofDim[Option[Color]](size, size)

  def get(x: Int, y: Int): Option[Color]             = grid(x)(y)
  def get(intersection: Intersection): Option[Color] = grid(intersection.x)(intersection.y)

  def set(x: Int, y: Int, color: Option[Color]): Unit = grid(x)(y) = color
  def set(intersection: Intersection, color: Option[Color]): Unit =
    grid(intersection.x)(intersection.y) = color
}
