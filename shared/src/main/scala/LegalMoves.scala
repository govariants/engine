package org.govariants.engine;

import scalajs.js.annotation.{ JSExportAll, JSExportTopLevel }

@JSExportAll
@JSExportTopLevel("LegalMoves")
case class LegalMoves(val legal: List[Intersection], val ko: List[Intersection])
