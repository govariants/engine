package org.govariants.engine;

import collection.immutable.Set
import scalajs.js.annotation.{ JSExportAll, JSExportTopLevel }

@JSExportAll
@JSExportTopLevel("LegalMoves")
case class LegalMoves(legal: Set[Intersection] = Set(), ko: Set[Intersection] = Set())

sealed trait PlayabilityType
case object Legal extends PlayabilityType
case object Illegal extends PlayabilityType
case object Ko extends PlayabilityType
