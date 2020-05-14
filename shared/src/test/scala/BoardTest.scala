package org.govariants.engine

import org.scalatest.FunSuite

class BoardTest extends FunSuite {

  test("Simple capture one stone") {
    val board_1_str = """+ + + + + + + + +
                        |+ X X + + + + + +
                        |+ + + + + + + + +
                        |+ + O X + + + + +
                        |+ + + O X + + + +
                        |+ + + X + + + + +
                        |+ + + + + + + + +
                        |+ + + + + + + + +
                        |+ + + + + + + + +""".stripMargin
    val board_1     = board_from_string(board_1_str)

    board_1.add_stone(2, 4, Black)

    val board_2_str = """+ + + + + + + + +
                        |+ X X + + + + + +
                        |+ + + + + + + + +
                        |+ + O X + + + + +
                        |+ + X + X + + + +
                        |+ + + X + + + + +
                        |+ + + + + + + + +
                        |+ + + + + + + + +
                        |+ + + + + + + + +""".stripMargin
    val board_2     = board_from_string(board_2_str)

    assert(board_1.toString() == board_2.toString())
  }

  test("Multiple stones capture") {
    val board_1_str = """+ + + + X X X + +
                        |+ X + X O O O X +
                        |+ + X + + X O X +
                        |+ + O X O X X + +
                        |+ + X O O X + + +
                        |+ + + X O X + + +
                        |+ + + + X + + + +
                        |+ + + + + + + + +
                        |+ + + + + + + + +""".stripMargin
    val board_1     = board_from_string(board_1_str)

    board_1.add_stone(4, 2, Black)

    val board_2_str = """+ + + + X X X + +
                        |+ X + X + + + X +
                        |+ + X + X X + X +
                        |+ + O X + X X + +
                        |+ + X + + X + + +
                        |+ + + X + X + + +
                        |+ + + + X + + + +
                        |+ + + + + + + + +
                        |+ + + + + + + + +""".stripMargin
    val board_2     = board_from_string(board_2_str)

    assert(board_1.toString() == board_2.toString())
  }

  def board_from_string(board_str: String): Board = {
    val board = new Board(board_str.split('\n').length)
    var j     = 0
    for (line <- board_str.split('\n')) {
      var i = 0
      assert(line.split(" ").length == board.size, "Board size is inconsistent")
      for (char <- line.split(' ')) {
        char match {
          case "+" => board.grid(i)(j) = None
          case "X" => board.grid(i)(j) = Some(Black)
          case "O" => board.grid(i)(j) = Some(White)
          case _   => assert(false, "Board char not recognized")
        }
        i += 1
      }
      j += 1
    }
    board.compute_groups_from_scratch()
    board
  }
}
