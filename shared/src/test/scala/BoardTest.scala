package org.govariants.engine

import org.scalatest.funsuite.AnyFunSuite
import scala.collection.mutable.ListBuffer

class BoardTest extends AnyFunSuite {

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
    val board_1 = board_from_string(board_1_str)

    board_1.add_stone(Intersection(2, 4), Black)

    val board_2_str = """+ + + + + + + + +
                        |+ X X + + + + + +
                        |+ + + + + + + + +
                        |+ + O X + + + + +
                        |+ + X + X + + + +
                        |+ + + X + + + + +
                        |+ + + + + + + + +
                        |+ + + + + + + + +
                        |+ + + + + + + + +""".stripMargin
    val board_2 = board_from_string(board_2_str)

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
    val board_1 = board_from_string(board_1_str)

    board_1.add_stone(Intersection(4, 2), Black)

    val board_2_str = """+ + + + X X X + +
                        |+ X + X + + + X +
                        |+ + X + X X + X +
                        |+ + O X + X X + +
                        |+ + X + + X + + +
                        |+ + + X + X + + +
                        |+ + + + X + + + +
                        |+ + + + + + + + +
                        |+ + + + + + + + +""".stripMargin
    val board_2 = board_from_string(board_2_str)

    assert(board_1.toString() == board_2.toString())
  }

  test("Full 9x9 game") {
    val sgf_string = sgfs.SGF1.content
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

  test("Compute score of full 9x9 game") {
    val sgf_string = sgfs.SGF1.content
    val sgf_parser: SGFParser = new SGFParser(sgf_string)

    val board = sgf_parser.build_board()
    val dead_stones =
      ListBuffer(Intersection(5, 7), Intersection(6, 6), Intersection(7, 7), Intersection(6, 8))
    println(board.score(7, dead_stones))
    assert(board.score(7, dead_stones) == ((43, 45)))
  }

  test("Simple Ko is forbidden") {
    val board_1_str = """+ + + + X X X + +
                        |+ X + X O O O X +
                        |+ + X + + X O X +
                        |+ + O + + + + + +
                        |+ + X X O X + + +
                        |+ + X O + O + + +
                        |+ + + X O + + + +
                        |+ + + + + + + + +
                        |+ + + + + + + + +""".stripMargin
    val board_1 = board_from_string(board_1_str)

    board_1.add_stone(Intersection(4, 5), Black)

    assert(!board_1.legal_moves(White).contains(Intersection(3, 5)))
  }

  def board_from_string(board_str: String): Board = {
    val board = new Board(board_str.split('\n').length)
    var j = 0
    for (line <- board_str.split('\n')) {
      var i = 0
      assert(line.split(" ").length == board.size, "Board size is inconsistent")
      for (char <- line.split(' ')) {
        char match {
          case "+" =>
          case "X" => board.add_stone(Intersection(i, j), Black)
          case "O" => board.add_stone(Intersection(i, j), White)
          case _   => assert(false, "Board char not recognized")
        }
        i += 1
      }
      j += 1
    }
    board
  }
}
