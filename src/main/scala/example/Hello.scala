package example

object Hello extends Greeting with App {
  import game_p.Game

  println(greeting)

  var game = new Game(9)
  game.start()
  game.display()

  game.play(2, 2)
  game.display()

  game.play(6, 5)
  game.display()
}

trait Greeting {
  lazy val greeting: String = "hello"
}
