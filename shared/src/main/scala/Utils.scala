package org.govariants.engine

import org.govariants.sgfparser.{ GameTree, Move, Pass, Rectangular, Square }

object Utils {
  def main_line_to_board(game_tree: GameTree): Board = {
    val board = new Board(game_tree.sequence(0).sz.getOrElse(Square(19)) match {
      case Square(size)               => size
      case Rectangular(columns, rows) => columns
    })

    for (node <- game_tree.main_line) {
      if (node.w.isDefined) {
        node.w.get match {
          case Move(column, row) => board.add_stone(Intersection(column, row), White)
          case Pass              =>
        }

      }
      if (node.b.isDefined) {
        node.b.get match {
          case Move(column, row) => board.add_stone(Intersection(column, row), Black)
          case Pass              =>
        }
      }

    }
    board
  }

}
