//scalaVersion := "3.3.0"
scalaVersion := "2.12.10"
//mainClass in (Compile, run) := Some("Main")
mainClass in Compile := Some("TerminalMazeGame")
libraryDependencies ++= Seq(
  "org.apache.spark" %% "spark-core" % "3.2.0",
  "org.apache.spark" %% "spark-sql" % "3.2.0"
)
resolvers += "Maven Central" at "https://repo1.maven.org/maven2"