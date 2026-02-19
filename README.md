# Multiplayer Matchmaking Simulation

**Language:** Java | **Concepts:** Generics, Custom Data Structures, Queue Theory

### Project Overview
A backend simulation of a video game matchmaking server (similar to Overwatch or WoW). It manages player wait times and role assignments using **custom-built Generic Queues**, proving an understanding of data structures beyond the standard Java API.

### Key Features
* **Custom Generics:** Implemented a type-safe `Queue<T>` class from scratch (using Linked Nodes) instead of using `java.util.LinkedList`.
* **Role Balancing Algorithm:** `RoleManager.java` dynamically assigns players to "Tank", "Healer", or "DPS" slots based on probability distributions.
* **Simulation Logic:** Tracks metrics like "Average Wait Time" and "Queue Length" to analyze matchmaking efficiency.

### Technical Structure
* `Queue.java`: A custom implementation of the Queue ADT using Generics.
* `Simulation.java`: The main engine that runs the tick-based game loop.
* `RoleManager.java`: Handles probability logic for player class selection.

### Usage
The simulation reads configuration data to determine player arrival rates and outputs a performance report (wait times per role).
