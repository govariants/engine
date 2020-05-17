package org.govariants.engine

import scala.io.StdIn.readLine
import java.util.Scanner

object Main extends App {
  val size = 9
  var game = new Game(size)
  game.start()
  game.display()

  while (true) {
    val move = get_move(size)
    if (!game.move_is_legal(move)) {
      println("Move is not legal")
    } else {
      game.play(move)
      game.display()
    }
  }

  def get_move(size: Int): Intersection = {
    var x: Int = 0
    var y: Int = 0

    do {
      val move = readLine("Move ?")
      val scanner = new Scanner(move).useDelimiter("")
      x = scanner.next().charAt(0) - 'a'
      y = scanner.nextInt() - 1
      println()
    } while (x < 0 || x >= size || y < 0 || y >= size)

    Intersection(x, y)
  }
}
