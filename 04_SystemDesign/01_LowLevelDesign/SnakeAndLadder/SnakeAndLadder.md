# Snake and Ladder Game — Low-Level Design

## Problem Statement

Design a Snake and Ladder game that supports:

- **Multiple players** (2-4) taking turns
- **Configurable board** with snakes and ladders at custom positions
- **Dice rolling** (one or two dice, configurable)
- **Win condition**: First player to reach or exceed the last cell wins
- **Exact landing rule** (optional): Player must roll exact number to land on the last cell

This problem tests your ability to model game state, enforce turn-based rules, and apply OOP principles cleanly.

---

## Design Patterns Used

| Pattern | Where | Why |
|---------|-------|-----|
| **State** | `GameState` (WAITING, IN_PROGRESS, FINISHED) | Game behaves differently in each state |
| **Strategy** | `DiceStrategy` | Different dice configurations (single die, double dice) |
| **Iterator** | Turn management | Cycle through players in order |
| **Observer** | Game events | Notify UI or log on snake bite, ladder climb, win |

---

## Complete Java Implementation

### Enums and Value Objects

```java
public enum GameState {
    WAITING,      // Players still joining
    IN_PROGRESS,  // Game running
    FINISHED;     // Someone won
}

public record Snake(int head, int tail) {
    public Snake {
        if (head <= tail) throw new IllegalArgumentException("Snake head must be above tail");
    }
}

public record Ladder(int bottom, int top) {
    public Ladder {
        if (bottom >= top) throw new IllegalArgumentException("Ladder bottom must be below top");
    }
}
```

### Dice (Strategy Pattern)

```java
public interface DiceStrategy {
    int roll();
    int getMaxRoll();
}

public class SingleDice implements DiceStrategy {
    private final Random random = new Random();

    @Override
    public int roll() {
        return random.nextInt(6) + 1; // 1-6
    }

    @Override
    public int getMaxRoll() { return 6; }
}

public class DoubleDice implements DiceStrategy {
    private final Random random = new Random();

    @Override
    public int roll() {
        return (random.nextInt(6) + 1) + (random.nextInt(6) + 1); // 2-12
    }

    @Override
    public int getMaxRoll() { return 12; }
}
```

### Player

```java
public class Player {
    private final String id;
    private final String name;
    private int position;

    public Player(String id, String name) {
        this.id = id;
        this.name = name;
        this.position = 0; // Off the board, will start at 1 on first move
    }

    public void moveTo(int position) {
        this.position = position;
    }

    public String getId() { return id; }
    public String getName() { return name; }
    public int getPosition() { return position; }

    @Override
    public String toString() {
        return name + " (pos: " + position + ")";
    }
}
```

### Board

```java
import java.util.*;

public class Board {
    private final int size;  // Total cells (e.g., 100 for a 10x10 board)
    private final Map<Integer, Integer> snakes;   // head → tail
    private final Map<Integer, Integer> ladders;  // bottom → top

    public Board(int size, List<Snake> snakeList, List<Ladder> ladderList) {
        this.size = size;
        this.snakes = new HashMap<>();
        this.ladders = new HashMap<>();

        for (Snake snake : snakeList) {
            validatePosition(snake.head(), "Snake head");
            validatePosition(snake.tail(), "Snake tail");
            if (snakes.containsKey(snake.head()) || ladders.containsKey(snake.head())) {
                throw new IllegalArgumentException(
                    "Position " + snake.head() + " already has a snake or ladder"
                );
            }
            snakes.put(snake.head(), snake.tail());
        }

        for (Ladder ladder : ladderList) {
            validatePosition(ladder.bottom(), "Ladder bottom");
            validatePosition(ladder.top(), "Ladder top");
            if (ladders.containsKey(ladder.bottom()) || snakes.containsKey(ladder.bottom())) {
                throw new IllegalArgumentException(
                    "Position " + ladder.bottom() + " already has a snake or ladder"
                );
            }
            ladders.put(ladder.bottom(), ladder.top());
        }
    }

    private void validatePosition(int position, String label) {
        if (position < 1 || position > size) {
            throw new IllegalArgumentException(
                label + " position " + position + " is out of board range [1, " + size + "]"
            );
        }
    }

    /**
     * Given a position, check if there is a snake or ladder and return the final position.
     */
    public int getFinalPosition(int position) {
        if (snakes.containsKey(position)) {
            int tail = snakes.get(position);
            System.out.printf("  🐍 Snake at %d! Sliding down to %d%n", position, tail);
            return tail;
        }
        if (ladders.containsKey(position)) {
            int top = ladders.get(position);
            System.out.printf("  🪜 Ladder at %d! Climbing up to %d%n", position, top);
            return top;
        }
        return position;
    }

    public int getSize() { return size; }
    public boolean isWinningPosition(int position) { return position >= size; }
}
```

### Game Event Observer

```java
public interface GameEventListener {
    void onPlayerMoved(Player player, int fromPos, int toPos, int diceRoll);
    void onSnakeBite(Player player, int fromPos, int toPos);
    void onLadderClimb(Player player, int fromPos, int toPos);
    void onPlayerWon(Player player);
    void onTurnSkipped(Player player, String reason);
}

public class ConsoleGameLogger implements GameEventListener {
    @Override
    public void onPlayerMoved(Player player, int fromPos, int toPos, int diceRoll) {
        System.out.printf("%s rolled %d: %d → %d%n", player.getName(), diceRoll, fromPos, toPos);
    }

    @Override
    public void onSnakeBite(Player player, int fromPos, int toPos) {
        System.out.printf("  🐍 %s bitten by snake! %d → %d%n", player.getName(), fromPos, toPos);
    }

    @Override
    public void onLadderClimb(Player player, int fromPos, int toPos) {
        System.out.printf("  🪜 %s climbed a ladder! %d → %d%n", player.getName(), fromPos, toPos);
    }

    @Override
    public void onPlayerWon(Player player) {
        System.out.printf("🏆 %s WINS the game!%n", player.getName());
    }

    @Override
    public void onTurnSkipped(Player player, String reason) {
        System.out.printf("⏭ %s turn skipped: %s%n", player.getName(), reason);
    }
}
```

### Game Engine

```java
import java.util.*;

public class SnakeAndLadderGame {
    private final Board board;
    private final DiceStrategy diceStrategy;
    private final List<Player> players;
    private final List<GameEventListener> listeners;
    private final boolean requireExactLanding;

    private GameState state;
    private int currentPlayerIndex;
    private Player winner;

    public SnakeAndLadderGame(Board board, DiceStrategy diceStrategy,
                               boolean requireExactLanding) {
        this.board = board;
        this.diceStrategy = diceStrategy;
        this.requireExactLanding = requireExactLanding;
        this.players = new ArrayList<>();
        this.listeners = new ArrayList<>();
        this.state = GameState.WAITING;
        this.currentPlayerIndex = 0;
    }

    public void addPlayer(Player player) {
        if (state != GameState.WAITING) {
            throw new IllegalStateException("Cannot add players after game has started");
        }
        if (players.size() >= 4) {
            throw new IllegalStateException("Maximum 4 players allowed");
        }
        players.add(player);
    }

    public void addEventListener(GameEventListener listener) {
        listeners.add(listener);
    }

    public void start() {
        if (players.size() < 2) {
            throw new IllegalStateException("Need at least 2 players to start");
        }
        state = GameState.IN_PROGRESS;
        currentPlayerIndex = 0;
        System.out.println("Game started with " + players.size() + " players!");
    }

    /**
     * Play one turn for the current player.
     * Returns the player who just played.
     */
    public Player playTurn() {
        if (state != GameState.IN_PROGRESS) {
            throw new IllegalStateException("Game is not in progress");
        }

        Player currentPlayer = players.get(currentPlayerIndex);
        int diceRoll = diceStrategy.roll();
        int currentPos = currentPlayer.getPosition();
        int newPos = currentPos + diceRoll;

        // Check if the new position exceeds the board
        if (newPos > board.getSize()) {
            if (requireExactLanding) {
                // Player stays in place
                for (GameEventListener l : listeners) {
                    l.onTurnSkipped(currentPlayer,
                        "Rolled " + diceRoll + " but needs exact " +
                        (board.getSize() - currentPos) + " to win");
                }
                advanceTurn();
                return currentPlayer;
            } else {
                newPos = board.getSize(); // Clamp to winning position
            }
        }

        // Move player
        currentPlayer.moveTo(newPos);
        for (GameEventListener l : listeners) {
            l.onPlayerMoved(currentPlayer, currentPos, newPos, diceRoll);
        }

        // Check for snake or ladder
        int finalPos = board.getFinalPosition(newPos);
        if (finalPos != newPos) {
            if (finalPos < newPos) {
                for (GameEventListener l : listeners) {
                    l.onSnakeBite(currentPlayer, newPos, finalPos);
                }
            } else {
                for (GameEventListener l : listeners) {
                    l.onLadderClimb(currentPlayer, newPos, finalPos);
                }
            }
            currentPlayer.moveTo(finalPos);
        }

        // Check win condition
        if (board.isWinningPosition(currentPlayer.getPosition())) {
            state = GameState.FINISHED;
            winner = currentPlayer;
            for (GameEventListener l : listeners) {
                l.onPlayerWon(currentPlayer);
            }
            return currentPlayer;
        }

        advanceTurn();
        return currentPlayer;
    }

    private void advanceTurn() {
        currentPlayerIndex = (currentPlayerIndex + 1) % players.size();
    }

    /**
     * Auto-play the entire game until someone wins.
     */
    public Player autoPlay() {
        start();
        int maxTurns = 1000; // Safety limit
        int turn = 0;
        while (state == GameState.IN_PROGRESS && turn < maxTurns) {
            playTurn();
            turn++;
        }
        return winner;
    }

    // Getters
    public GameState getState() { return state; }
    public Player getCurrentPlayer() { return players.get(currentPlayerIndex); }
    public Player getWinner() { return winner; }
    public List<Player> getPlayers() { return Collections.unmodifiableList(players); }
}
```

### Main — Example Game

```java
public class Main {
    public static void main(String[] args) {
        // Create a 100-cell board with snakes and ladders
        List<Snake> snakes = List.of(
            new Snake(99, 10),
            new Snake(70, 30),
            new Snake(52, 29),
            new Snake(55, 3),
            new Snake(95, 56)
        );

        List<Ladder> ladders = List.of(
            new Ladder(2, 23),
            new Ladder(8, 34),
            new Ladder(20, 77),
            new Ladder(41, 79),
            new Ladder(74, 92)
        );

        Board board = new Board(100, snakes, ladders);
        DiceStrategy dice = new SingleDice();
        SnakeAndLadderGame game = new SnakeAndLadderGame(board, dice, false);

        game.addPlayer(new Player("p1", "Alice"));
        game.addPlayer(new Player("p2", "Bob"));
        game.addPlayer(new Player("p3", "Charlie"));
        game.addEventListener(new ConsoleGameLogger());

        Player winner = game.autoPlay();
        System.out.println("Game over! Winner: " + winner.getName());
    }
}
```

---

## Interview Discussion Points

1. **Why use records for Snake and Ladder?**
   - They are immutable value objects. A snake is fully defined by its head and tail positions. Using Java records gives us equals, hashCode, toString, and immutability for free.

2. **How would you add a "roll again on 6" rule?**
   - Add a `rollAgainOnMax` flag. In `playTurn()`, after rolling, check if the roll equals `diceStrategy.getMaxRoll()`. If so, do not advance the turn (`advanceTurn()` is skipped).

3. **How would you extend this for multiplayer online?**
   - Use the Command pattern: each `playTurn()` becomes a `TurnCommand` that is serialized and sent to a game server. The server applies the command, computes the outcome, and broadcasts the result to all clients. The `DiceStrategy` would be server-side only to prevent cheating.

4. **Thread safety considerations?**
   - For a local game, no thread safety is needed (turns are sequential). For an online game, the game state would be protected by a lock or processed through a single-threaded event loop (actor model).

