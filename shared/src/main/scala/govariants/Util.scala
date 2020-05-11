package org.govariants.engine

@JSExportTopLevel("Color")
sealed trait Color

@JSExportTopLevel("Black")
case object Black extends Color
@JSExportTopLevel("White")
case object White extends Color
