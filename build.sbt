name := "bulletf"

version := "0.1.0-SNAPSHOT"

scalaVersion := "2.10.3"

libraryDependencies += "org.scalatest" %% "scalatest" % "2.0" % "test"

scalacOptions += "-feature"

//incOptions := incOptions.value.withNameHashing(true)

javaOptions ++= Seq("-verbose:gc", "-Dfile.encoding=UTF-8")

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
