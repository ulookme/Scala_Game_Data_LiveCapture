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
  case class Move(x: Int, y: Int, hitWall: Int)
  val playerMoves = ListBuffer[Move]()
  var playerPosition = (1, 1) // Position initiale du joueur (ligne, colonne)

  def printMaze(): Unit = {
    for (i <- maze.indices) {
      for (j <- maze(i).indices) {
        if ((i, j) == playerPosition) print("P")
        else print(if (maze(i)(j) == 1) "#" else " ")
      }
      println()
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
  // Jeu principal
  var input: String = ""
  while (input != "exit") {
    print("\u001b[2J") // Nettoyer la console
    printMaze()
    println("Move (w/a/s/d) or 'exit' to quit:")
    input = readLine.trim
    if (input.length == 1) movePlayer(input.head)
  }
  // Afficher les mouvements du joueur
  println("Player Moves:")
  for ((index, move) <- playerMoves.zipWithIndex) {
    println(s"Move $index: $move")
  }
  //mainLoop()

  // Création d'un DataFrame à partir de la liste des mouvements
  val movesDF = playerMoves.toDF()

  // Affichage du DataFrame
  movesDF.show()

  // Exemple d'opération Spark SQL sur le DataFrame
  movesDF.filter($"hitWall" === 1).show() // Afficher les mouvements qui ont heurté un mur

  // Arrêt de la session Spark
  spark.stop()
}