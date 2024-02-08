file://<WORKSPACE>/src/main/scala/game/Game.scala
### java.lang.IndexOutOfBoundsException: 0

occurred in the presentation compiler.

action parameters:
offset: 2768
uri: file://<WORKSPACE>/src/main/scala/game/Game.scala
text:
```scala
import scala.io.StdIn.readLine
import scala.collection.mutable.ListBuffer
import org.apache.spark.sql.{DataFrame, SparkSession}
import org.apache.spark.sql.functions._
import org.apache.spark.sql.SparkSession

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
    val isWall = maze(newPosition._1)(@@)
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
```



#### Error stacktrace:

```
scala.collection.LinearSeqOps.apply(LinearSeq.scala:131)
	scala.collection.LinearSeqOps.apply$(LinearSeq.scala:128)
	scala.collection.immutable.List.apply(List.scala:79)
	dotty.tools.dotc.util.Signatures$.countParams(Signatures.scala:501)
	dotty.tools.dotc.util.Signatures$.applyCallInfo(Signatures.scala:186)
	dotty.tools.dotc.util.Signatures$.computeSignatureHelp(Signatures.scala:94)
	dotty.tools.dotc.util.Signatures$.signatureHelp(Signatures.scala:63)
	scala.meta.internal.pc.MetalsSignatures$.signatures(MetalsSignatures.scala:17)
	scala.meta.internal.pc.SignatureHelpProvider$.signatureHelp(SignatureHelpProvider.scala:51)
	scala.meta.internal.pc.ScalaPresentationCompiler.signatureHelp$$anonfun$1(ScalaPresentationCompiler.scala:388)
```
#### Short summary: 

java.lang.IndexOutOfBoundsException: 0