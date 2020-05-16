package org.govariants.engine

import scalajs.js.annotation.{ JSExportAll, JSExportTopLevel }

@JSExportAll
@JSExportTopLevel("SGFParser")
class SGFParser(val sgf_string: String) {
  val move_regex       = """;(W|B)\[([a-z])([a-z])\]""".r
  val board_size_regex = """SZ\[(\d{1,2}|\d{1,2}:\d{1,2})\]""".r

  def build_board(): Board = {
    val size = board_size_regex.findFirstMatchIn(sgf_string) match {
      case Some(m) => get_size(m.group(1))
      case None    => 0
    }
    assert(size > 0, "No valid board size found in sgf")
    val board = new Board(size)

    for (m <- move_regex.findAllMatchIn(sgf_string)) {
      board.add_stone(
        Intersection(letter_to_int(m.group(2)), letter_to_int(m.group(3))),
        letter_to_color(m.group(1))
      )
    }

    board
  }

  def get_size(string: String): Int = {
    val strings = string.split(':')
    strings.length match {
      case 1 => assert(strings(0).toInt <= 19 && strings(0).toInt >= 5, "Board size must be in [5, 19]")
      case 2 => assert(strings(0).toInt == strings(1).toInt, "Non-square board size not supported atm")
      case _ => assert(false, "Could not read board size")
    }
    return strings(0).toInt
  }

  def letter_to_int(letter: String): Int = {
    (letter.charAt(0) - 'a').asInstanceOf[Int]
  }

  def letter_to_color(letter: String): Color = {
    letter match {
      case "B" => Black
      case "W" => White
    }
  }
}
