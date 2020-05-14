package org.govariants.engine

package object datastructures {
  implicit object JSGridBuilder extends GridBuilder {
    def build(size: Int): JSGrid = new JSGrid(size)
  }
}
