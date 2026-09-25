# Terminal-Impostor

# The Impostor

A multiplayer social-deduction game played entirely in the terminal. Built in Java with raw sockets and multithreading. No framework, no GUI, just a server and players connected over TCP.

```
████████╗██╗  ██╗███████╗    ██╗███╗   ███╗██████╗  ██████╗ ███████╗████████╗ ██████╗ ██████╗
╚══██╔══╝██║  ██║██╔════╝    ██║████╗ ████║██╔══██╗██╔═══██╗██╔════╝╚══██╔══╝██╔═══██╗██╔══██╗
   ██║   ███████║█████╗      ██║██╔████╔██║██████╔╝██║   ██║███████╗   ██║   ██║   ██║██████╔╝
   ██║   ██╔══██║██╔══╝      ██║██║╚██╔╝██║██╔═══╝ ██║   ██║╚════██║   ██║   ██║   ██║██╔══██╗
   ██║   ██║  ██║███████╗    ██║██║ ╚═╝ ██║██║     ╚██████╔╝███████║   ██║   ╚██████╔╝██║  ██║
   ╚═╝   ╚═╝  ╚═╝╚══════╝    ╚═╝╚═╝     ╚═╝╚═╝      ╚═════╝ ╚══════╝   ╚═╝    ╚═════╝ ╚═╝  ╚═╝
```

## How to play

Every player gets the name of a famous film or series — except the impostor, who gets nothing and has to bluff.

1. Four players connect to the server and enter their names.
2. One player is secretly chosen as the impostor.
3. Everyone else receives the same prompt (e.g. *Star Wars*), shown with ASCII art.
4. Over **three rounds**, each player types a word or clue about the prompt. Clues are broadcast to everyone else. The impostor has to read the room and blend in.
5. Everyone votes for who they think the impostor is.
6. The most-voted player is eliminated:
   - **Impostor eliminated:** the players win.
   - **Innocent player eliminated:** the impostor wins.
   - **Tie:** the game ends in a draw.

The prompt pool includes *The Lord of the Rings*, *Star Wars*, *The Matrix*, *Back to the Future*, *The Godfather*, *Harry Potter*, *Toy Story*, *The Simpsons*, *E.T.* and *A Clockwork Orange*.

## Getting started

### Requirements

- Java 8 or later (tested on Java 21)
- `nc` (netcat) or `telnet` on each player's machine

### Build

From the `ImpostorTextBased` folder:

```bash
javac -cp lib/prompt-view-0.2.1-SNAPSHOT.jar -d out $(find src -name "*.java")
```

### Start the server

```bash
java -cp "out:lib/prompt-view-0.2.1-SNAPSHOT.jar" io.codeforall.supernans.Main
```

On Windows, use `;` instead of `:` as the classpath separator.

The server listens on port **8080** and waits until four players have joined and all confirmed they are ready.

### Join as a player

Each player opens a terminal and connects:

```bash
nc localhost 8080
```

Replace `localhost` with the server's IP address to play across machines on the same network.

## How it works

```
                 ┌─────────────────────┐
  Player 1 ─TCP─▶│                     │
  Player 2 ─TCP─▶│   Server (port 8080)│──▶ ImpostorGame
  Player 3 ─TCP─▶│   one thread per    │    (impostor selection,
  Player 4 ─TCP─▶│   connected player  │     prompts)
                 └─────────────────────┘
```

| Class | Responsibility |
|---|---|
| `Main` | Starts the server, triggers impostor selection and the prompt, and drives the voting and elimination loop. |
| `Server` | Accepts socket connections, holds shared game state (players, votes) and exposes synchronized checks such as "are all players ready?" and "has everyone voted?". |
| `ServerWorker` | One per player, running on its own thread. Handles that player's name, ready check, clue rounds and vote, and broadcasts clues to the other players. |
| `ImpostorGame` | Holds the prompt library, picks the impostor at random and sends the prompt to every non-impostor. |

**Concepts practised:**
- TCP networking with `ServerSocket` and `Socket`
- Multithreading, with one `Runnable` worker per client
- Shared state protected by `synchronized` methods and `volatile` flags
- Broadcasting messages from one client to all others
- Input validation with the `prompt-view` library (e.g. votes must match an existing player name)

## What I'd improve today

This was one of my first multithreaded projects. Looking back at it, I'd change:

- **Configurable settings.** Player count (4) and port (8080) are hardcoded; they should be command-line arguments.
- **String comparison.** Some player-name checks use `==` instead of `.equals()`, which compares references rather than content in Java.
- **Busy waiting.** The main loop polls continuously for votes. A `wait()`/`notify()` handshake or a `CountDownLatch` would be more efficient.
- **Safe list modification.** Removing a player from the worker list while iterating over it should use an `Iterator` or a concurrent collection.
- **Build tooling.** Adding Maven or Gradle would replace the manual `javac` command and manage the dependency.

## Built with

- Java
- [prompt-view](ImpostorTextBased/lib/) — a terminal input library used at the Code for All_ bootcamp

Built during the [Code for All_](https://codeforall.com/) full stack bootcamp.
