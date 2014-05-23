name := "bulletf"

version := "0.1.0-SNAPSHOT"

scalaVersion := "2.11.1"

libraryDependencies <++= scalaVersion { v => Seq(
  "org.scalatest" %% "scalatest" % "2.1.6" % "test",
  "org.scala-lang.modules" %% "scala-parser-combinators" % "1.0.1",
  "org.scala-lang.modules" %% "scala-xml" % "1.0.1"
)}


scalacOptions += "-feature"

//incOptions := incOptions.value.withNameHashing(true)

javaOptions ++= Seq("-verbose:gc", "-Xloggc:./gc.log", "-XX:+PrintGCDetails", "-XX:+PrintTenuringDistribution", "-Dfile.encoding=UTF-8")

initialCommands in console += "import bulletf._"

initialCommands in (Compile, consoleQuick) <<= initialCommands in Compile

Seq(LWJGLPlugin.lwjglSettings: _*)

compile in Compile <<= (compile in Compile result) map {
  case Inc(inc: Incomplete) =>
    Sound.play("sound/cut01.wav")
    throw inc
  case Value(v) =>
    Sound.play("sound/pickup01.wav")
    v
}

LWJGLPlugin.lwjgl.version := "2.9.1"
