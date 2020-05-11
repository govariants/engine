package govariants

import scalajs.js.annotation.{ JSExportAll, JSExportTopLevel }

@JSExportAll
@JSExportTopLevel("StoneGroups")
class StoneGroups() {

//  def add_stone (val x: Int, val y: Int) = {
//    2
//  }
}

@JSExportAll
@JSExportTopLevel("Stone")
class Stone(val x: Int, val y: Int, val next: Stone) {}
