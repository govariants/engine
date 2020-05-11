package govariants

import scala.collection.mutable.HashMap
import scala.collection.mutable.ListBuffer

import scalajs.js.annotation.{ JSExportAll, JSExportTopLevel }

@JSExportAll
@JSExportTopLevel("Board")
class Board(val size: Int) {

  val EMPTY = 0
  val BLACK = 1
  val WHITE = 2

  var grid = Array.ofDim[Int](size, size)

  for (i <- 0 until size) {
    for (j <- 0 until size) {
      grid(i)(j) = EMPTY
    }
  }
  var groups: HashMap[Int, ListBuffer[Intersection]] = HashMap()
  var groups_color: HashMap[Int, Int]                = HashMap()

  def display() = {
    for (j <- 0 until size) {
      for (i <- 0 until size) {
        grid(i)(j) match {
          case EMPTY => print("+ ")
          case BLACK => print("X ")
          case WHITE => print("O ")
        }
      }
      println()
    }
  }
}

@JSExportAll
@JSExportTopLevel("Intersection")
case class Intersection(val x: Int, val y: Int)
