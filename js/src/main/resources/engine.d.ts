export declare function Game(size: number, komi: number): GameClass;

interface GameClass {
  turn: string;
  legal_moves: number[][][];
  start(): string[];
  move_is_legal(column: number, row: number): boolean;
  play(column: number, row: number): string[];
  compute_score(dead_stones: Iterable<number[]>): number[];
  switch_turn(): void;
  display(): void;
}
