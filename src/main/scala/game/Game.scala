import scala.io.StdIn.readLine
import scala.collection.mutable.ListBuffer
import org.apache.spark.sql.{DataFrame, SparkSession}
import org.apache.spark.sql.functions._
import org.apache.spark.sql.SparkSession
import scala.util.Random
import util.MazeUtils


object TerminalMazeGame extends App {
  // Initialisation de la session Spark
  val spark = SparkSession.builder()
    .appName("Terminal Maze Game")
    .config("spark.master", "local")
    .getOrCreate()
  import spark.implicits._
  // Représentation du labyrinthe avec des 0 (chemin libre) et des 1 (mur)
  val maze = Array(
    Array(1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1),
    Array(1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1),
    Array(1, 0, 1, 1, 1, 0, 1, 1, 1, 1, 1, 1, 1, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 1),
    Array(1, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 1, 0, 0, 1, 1, 1, 1, 0, 1),
    Array(1, 0, 1, 1, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 0, 0, 1, 1, 1, 1, 0, 1),
    Array(1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 0, 1, 1, 1, 1, 0, 1, 0, 1),
    Array(1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 0, 1, 1, 1, 1, 0, 1, 0, 1),
    Array(1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 1, 0, 0, 0, 0, 1, 1, 0, 0, 0, 1, 1, 0, 1, 0, 1),
    Array(1, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 1, 1, 0, 0, 0, 0, 1, 1, 1, 1, 0, 1, 1, 0, 1, 0, 1),
    Array(1, 0, 0, 0, 0, 0, 0, 1, 1, 1, 1, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1),
    Array(1, 0, 0, 0, 0, 0, 0, 1, 1, 0, 1, 1, 0, 0, 0, 0, 0, 1, 1, 1, 1, 0, 1, 1, 1, 1, 0, 1),
    Array(1, 0, 0, 0, 0, 0, 0, 1, 1, 1, 1, 1, 0, 0, 0, 0, 0, 1, 1, 1, 0, 0, 1, 1, 1, 1, 0, 1),
    Array(1, 0, 0, 0, 0, 0, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 0, 1, 1, 1, 1, 0, 1),
    Array(1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 1),
    Array(1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1)
    )

  def placeReward(maze: Array[Array[Int]]): (Int, Int) = {
    for (i <- 1 until maze.length - 1) {
      for (j <- 1 until maze(i).length - 1) {
        if (maze(i)(j) == 0 && // Vérifie que la position actuelle est libre
            maze(i-1)(j) == 1 && // Vérifie les murs autour
            maze(i+1)(j) == 1 &&
            maze(i)(j-1) == 1 &&
            maze(i)(j+1) == 1) {
          return (i, j) // Retourne la première position qui correspond
        }
      }
    }
    (1, 3) // Fallback si aucune position valide n'est trouvée, ajustez selon votre labyrinthe
  }
  // Structure pour stocker les mouvements du joueur
  // Structure pour stocker les mouvements du joueur
  case class Move(x: Int, y: Int, hitWall: Int)
  val playerMoves = ListBuffer[Move]()
  var playerPosition = (1, 1) // Position initiale du joueur
  var enemyPosition = (maze.length - 2, maze(0).length - 2) // Position initiale de l'ennemi à l'opposé
  var rewardPosition = placeReward(maze) // Exemple de position, à ajuster selon la structure de votre labyrinthe
  
  def printMaze(): Unit = {
    for (i <- maze.indices) {
      for (j <- maze(i).indices) {
        print((i, j) match {
          case pos if pos == playerPosition => "P"
          case pos if pos == enemyPosition => "E"
          case pos if pos == rewardPosition => "R"
          case _ if maze(i)(j) == 1 => "#"
          case _ => " "
        })
      }
      println() // Assurez-vous de passer à une nouvelle ligne après chaque ligne du labyrinthe.
    }
  }




  def movePlayer(move: Char): Unit = {
    val (x, y) = playerPosition
    val newPosition = move match {
      case 'e' => (x - 1, y)
      case 'x' => (x + 1, y)
      case 's' => (x, y - 1)
      case 'd' => (x, y + 1)
      case _ => playerPosition
    }

    //determiner si la nouvelle position est un mur
    val isWall = maze(newPosition._1)(newPosition._2) == 1
    //Mettre a jour la position du joueur seulement s'il ne s'agit pas d'un mure 
    if (!isWall){
        playerPosition = newPosition
        playerMoves += Move(newPosition._1, newPosition._2, 0) //hitWall est zero car pas de mure 
    }else{
        // s'il rencontre un mur, n'actualise pas la position du joueur mais enregistre dans hitWall 1
        playerMoves += Move(x, y, 1)//utiliser la position actuelle, car le deplacement ver le mure echoue
        MazeUtils.modifyMazeAtRandom(maze, playerPosition._1, playerPosition._2, playerMoves.length)
    }
  }
  // Nouvelle fonction pour déplacer l'ennemi
  def moveEnemyTowardsPlayer(): Unit = {
    val (newX, newY) = MazeUtils.getEnemyMoveTowardsPlayer(enemyPosition, playerPosition, maze)
    if (maze(newX)(newY) == 0) { // S'assurer que la nouvelle position est libre
      enemyPosition = (newX, newY)
    }
  }

  def gameLoop(): Unit = {
    var input = ""
    while (input != "exit" && playerPosition != enemyPosition) {
      printMaze()
      println("Move (w/a/s/d) or 'exit' to quit:")
      input = readLine.trim

      if (input.nonEmpty && input.length == 1) {
        movePlayer(input.head)
        moveEnemyTowardsPlayer()

        println(s"Debug: Player Position: $playerPosition, Reward Position: $rewardPosition") // Debug print

        if (playerPosition == enemyPosition) {
          println("Caught by the enemy! Game Over.")
          return
        } else if (playerPosition == rewardPosition) {
          println("You found the reward! You win!")
          return // Or set input = "exit" to exit the loop gracefully
        }
      }
    }
  }
  gameLoop()

  // Création d'un DataFrame à partir de la liste des mouvements
  val movesDF = playerMoves.toDF()
  movesDF.show()

  spark.stop()
}