package org.govariants.engine;
package datastructures;

import scalajs.js.{ Array => ScalaJSArray }
import scalajs.js.annotation.{ JSExportAll, JSExportTopLevel }

@JSExportAll
@JSExportTopLevel("JSGrid")
class JSGrid[T](grid_size: Int, initial_value: T) extends Grid[T](grid_size, initial_value) {

  protected val grid_builder = JSGridBuilder
  val grid = new ScalaJSArray[ScalaJSArray[T]]()

  for (i <- 0 until grid_size) {
    val row = new ScalaJSArray[T]()
    row.padToInPlace(grid_size, initial_value)
    grid.append(row)
  }

  def get(x: Int, y: Int): T = grid(x)(y)
  def get(intersection: Intersection): T = grid(intersection.x)(intersection.y)

  def set(x: Int, y: Int, item: T): Unit = grid(x)(y) = item
  def set(intersection: Intersection, item: T): Unit =
    grid(intersection.x)(intersection.y) = item
}

object JSGridBuilder extends GridBuilder {
  def build[T](grid_size: Int, initial_value: T): JSGrid[T] = new JSGrid(grid_size, initial_value)
}
