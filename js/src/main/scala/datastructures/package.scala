package org.govariants.engine

package object datastructures {
  implicit object JSGridBuilder extends GridBuilder {
    def build[T](size: Int, initial_value: T): JSGrid[T] = new JSGrid(size, initial_value)
  }
}
