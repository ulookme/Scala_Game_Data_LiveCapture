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
    Array(1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 1, 1, 0, 0, 0, 1, 1, 0, 1, 0, 1),
    Array(1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 1, 0, 0, 0, 0, 1, 1, 1, 1, 0, 1, 1, 0, 1, 0, 1),
    Array(1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 1, 0, 0, 0, 0, 1, 1, 1, 1, 0, 0, 0, 0, 0, 0, 1),
    Array(1, 0, 0, 0, 0, 0, 0, 0, 1, 1, 1, 1, 1, 0, 0, 0, 0, 1, 1, 1, 1, 0, 1, 1, 1, 1, 0, 1),
    Array(1, 0, 0, 0, 0, 0, 0, 0, 1, 1, 1, 1, 0, 0, 0, 0, 0, 1, 1, 1, 0, 0, 1, 1, 1, 1, 0, 1),
    Array(1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 1, 0, 0, 1, 1, 1, 1, 0, 1),
    Array(1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 1),
    Array(1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1)
    )
  // Structure pour stocker les mouvements du joueur
  // Structure pour stocker les mouvements du joueur
  case class Move(x: Int, y: Int, hitWall: Int)
  val playerMoves = ListBuffer[Move]()
  var playerPosition = (1, 1) // Position initiale du joueur
  var enemyPosition = (maze.length - 2, maze(0).length - 2) // Position initiale de l'ennemi à l'opposé

  def printMaze(): Unit = {
    for (i <- maze.indices) {
      for (j <- maze(i).indices) {
        print((i, j) match {
          case pos if pos == playerPosition => "P"
          case pos if pos == enemyPosition => "E"
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

        if (playerPosition == enemyPosition) {
          println("Caught by the enemy! Game Over.")
          return
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