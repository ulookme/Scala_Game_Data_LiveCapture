// MazeUtils.scala
package util

import scala.util.Random

object MazeUtils {
  def modifyMazeAtRandom(maze: Array[Array[Int]], excludeX: Int, excludeY: Int, steps: Int): Unit = {
    val innerXRange = 1 until maze.length - 1 // Exclure les bords extérieurs
    val innerYRange = 1 until maze(0).length - 1 // Exclure les bords extérieurs

    for (x <- innerXRange; y <- innerYRange if x != excludeX || y != excludeY) {
      // Calculer une probabilité basée sur le nombre de pas effectués
      val probability = calculateProbability(steps)

      // Décider aléatoirement si la cellule doit être modifiée en fonction de la probabilité calculée
      if (Random.nextDouble() < probability) {
        maze(x)(y) = if (maze(x)(y) == 1) 0 else 1
      }
    }
  }

  // Cette fonction détermine la probabilité de changer une cellule basée sur le nombre de pas
  def calculateProbability(steps: Int): Double = {
    // Exemple: plus le joueur a fait de pas, plus la probabilité est élevée
    Math.min(1.0, steps / 1000.0) // Assurez-vous que la probabilité ne dépasse pas 1
  }

  // Calcul du mouvement de l'ennemi vers le joueur
  def getEnemyMoveTowardsPlayer(enemyPosition: (Int, Int), playerPosition: (Int, Int), maze: Array[Array[Int]]): (Int, Int) = {
    val directions = Seq((-1, 0), (1, 0), (0, -1), (0, 1)) // Haut, Bas, Gauche, Droite
    val validMoves = directions.map { case (dx, dy) =>
      (enemyPosition._1 + dx, enemyPosition._2 + dy)
    }.filter { case (newX, newY) =>
      newX >= 0 && newX < maze.length && newY >= 0 && newY < maze(0).length && maze(newX)(newY) == 0
    }

    if (validMoves.isEmpty) {
      enemyPosition // Aucun mouvement possible
    } else {
      // Choisissez le mouvement qui rapproche le plus l'ennemi du joueur en utilisant la distance Manhattan comme heuristique
      validMoves.minBy { case (newX, newY) =>
        Math.abs(newX - playerPosition._1) + Math.abs(newY - playerPosition._2)
      }
    }
  }
}
