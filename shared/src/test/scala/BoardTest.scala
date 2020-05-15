package org.govariants.engine

import scala.io.Source
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

  test("Full 9x9 game") {
    val sgf_file = Source.fromFile("9x9.sgf")
    val sgf_string =  sgf_file.getLines.mkString
    sgf_file.close
    val sgf_parser: SGFParser = new SGFParser(sgf_string)

    val board = sgf_parser.build_board()

    val target_board_str = """|+ + + + O O X + +
                              |+ + O + O X X X +
                              |+ O + O O O X + +
                              |+ O O X X O X + +
                              |+ O O O X X + X X
                              |+ O + O O X X + X
                              |+ + O X X X O X +
                              |+ O X X X O + O X
                              |+ O O X X + O + +""".stripMargin
    val target_board = board_from_string(target_board_str)

    assert(board.toString == target_board.toString())
  }

  def board_from_string(board_str: String): Board = {
    val board = new Board(board_str.split('\n').length)
    var j     = 0
    for (line <- board_str.split('\n')) {
      var i = 0
      assert(line.split(" ").length == board.size, "Board size is inconsistent")
      for (char <- line.split(' ')) {
        char match {
          case "+" => board.grid.set(i, j, None)
          case "X" => board.grid.set(i, j, Some(Black))
          case "O" => board.grid.set(i, j, Some(White))
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
