package org.govariants.engine;
package datastructures;

import scalajs.js.{ Array => ScalaJSArray }
import scalajs.js.annotation.{ JSExportAll, JSExportTopLevel }

@JSExportAll
@JSExportTopLevel("JSGrid")
class JSGrid(size: Int) extends Grid(size) {

  val grid: ScalaJSArray[ScalaJSArray[Option[Color]]] = new ScalaJSArray(size)

  for (i <- 0 until size) {
    grid(i) = new ScalaJSArray(size)
  }

  def get(x: Int, y: Int): Option[Color]             = grid(x)(y)
  def get(intersection: Intersection): Option[Color] = grid(intersection.x)(intersection.y)

  def set(x: Int, y: Int, color: Option[Color]): Unit = grid(x)(y) = color
  def set(intersection: Intersection, color: Option[Color]): Unit =
    grid(intersection.x)(intersection.y) = color
}
