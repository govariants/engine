package org.govariants.engine

sealed trait Color {
  val opposite: Color
}

case object Black extends Color {
  val opposite: Color = White
}
case object White extends Color {
  val opposite: Color = Black
}
