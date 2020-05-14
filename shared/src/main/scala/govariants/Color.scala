package org.govariants.engine
import scalajs.js.annotation.{ JSExportTopLevel }

sealed trait Color {
  val opposite: Color
}

@JSExportTopLevel("Black")
case object Black extends Color {
  val opposite: Color = White
}
@JSExportTopLevel("White")
case object White extends Color {
  val opposite: Color = Black
}
