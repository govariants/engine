package org.govariants.engine

package object datastructures {
  implicit object ScalaGridBuilder extends GridBuilder {
    def build(size: Int): ScalaGrid = new ScalaGrid(size)
  }
}
