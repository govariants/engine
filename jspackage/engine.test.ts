import { Game } from "./engine";

test("Create a game", () => {
  const game = Game(9, 6.5);
  const actions = game.start();
  expect(actions.length).toEqual(3);
});
