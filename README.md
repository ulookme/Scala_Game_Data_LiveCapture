# Terminal Maze Game with Scala and Spark

## Overview
Terminal Maze Game, a project designed to explore Scala and Apache Spark, is an interactive terminal-based maze game. Players navigate through a maze, dodging an intelligent enemy (E) on their quest to find a hidden reward (R). This game challenges players with dynamic maze structures and offers a unique integration of machine learning (ML) possibilities to enhance enemy AI.

## Game Concept

### Objective
- Navigate through the maze to find the reward without getting caught by the enemy.

### Controls
- Use `w` (up), `a` (left), `s` (down), `d` (right) to move the player (P) through the maze.

### Enemy
- The enemy (E) automatically moves towards the player, adapting its path to catch them.

### Reward
- A reward (R) is randomly placed within the maze, surrounded by walls. Reaching the reward wins the game.

## Key Features

- **Dynamic Maze Generation**: Leverages Scala for dynamic and manipulable maze structures.
- **Intelligent Enemy Movement**: Opens possibilities for using ML to enhance the AI's decision-making process.
- **Real-Time Interaction**: Engages players with live input reading for an interactive gaming experience.

## Using Spark

- **Data Processing**: Employs Spark for efficient movement analysis and future implementation of complex pathfinding algorithms.
- **ML Integration**: Spark's MLlib could train AI models for the enemy, evolving its strategies with gameplay data.

## ML Improvement Potential

- **Adaptive Enemy AI**: ML models can learn from player strategies, making the enemy more challenging.
- **Reward Placement Optimization**: Utilize ML to dynamically place the reward based on difficulty preferences.

## Documentation

### Getting Started

- **Prerequisites**: Scala, SBT, Apache Spark.
- **Installation**: Instructions on setting up your development environment and running the game.

### How to Play

- Navigate the maze using the control keys.
- Understand game elements: Player (P), Enemy (E), Reward (R), Walls (1), and Paths (0).
- Strategy tips for evading the enemy and finding the reward.

### Future Developments

- Integration of ML to enhance enemy AI, making each game a unique challenge.
- Potential for reward path optimization to adjust game difficulty dynamically.

## Conclusion

Terminal Maze Game showcases the power of Scala and Spark in creating interactive and complex game environments. With the future integration of ML, the game aims to offer an evolving challenge, adapting to player skills and strategies for an endlessly engaging experience.

---

**Enjoy navigating the maze, and may the odds be ever in your favor!**
