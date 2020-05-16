package org.govariants.engine;
package datastructures;

import scalajs.js.{ Array => ScalaJSArray }
import scalajs.js.annotation.{ JSExportAll, JSExportTopLevel }

@JSExportAll
@JSExportTopLevel("JSGrid")
class JSGrid[T](size: Int, initial_value: T) extends Grid[T](size, initial_value) {

  val grid = new ScalaJSArray[ScalaJSArray[T]]()

  for (i <- 0 until size) {
    val row = new ScalaJSArray[T]()
    row.padToInPlace(size, initial_value)
    grid.append(row)
  }

  def get(x: Int, y: Int): T             = grid(x)(y)
  def get(intersection: Intersection): T = grid(intersection.x)(intersection.y)

  def set(x: Int, y: Int, item: T): Unit = grid(x)(y) = item
  def set(intersection: Intersection, item: T): Unit =
    grid(intersection.x)(intersection.y) = item
}
