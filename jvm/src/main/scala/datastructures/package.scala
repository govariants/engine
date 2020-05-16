package org.govariants.engine

package object datastructures {
  implicit object ScalaGridBuilder extends GridBuilder {
    def build[T](size: Int, initial_value: T): ScalaGrid[T] = new ScalaGrid[T](size, initial_value)
  }
}
