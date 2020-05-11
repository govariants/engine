package govariants

import govariants.Game

object Main extends App {

  var game = new Game(9)
  game.start()
  game.display()

  game.play(2, 2)
  game.display()

  game.play(6, 5)
  game.display()
}
