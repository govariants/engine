package org.govariants.engine;

import collection.immutable.Set

case class LegalMoves(legal: Set[Intersection] = Set(), ko: Set[Intersection] = Set())

sealed trait PlayabilityType
case object Legal extends PlayabilityType
case object Illegal extends PlayabilityType
case object Ko extends PlayabilityType
