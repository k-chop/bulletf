name := "bulletf"

version := "0.1.0-SNAPSHOT"

scalaVersion := "2.10.1"

libraryDependencies += "org.scalatest" %% "scalatest" % "1.9.1" % "test"

scalacOptions += "-feature"

javaOptions in run ++= Seq("-verbose:gc", "-Dfile.encoding=UTF-8")

seq(lwjglSettings: _*)

compile in Compile <<= compile in Compile mapR {
  case Inc(inc: Incomplete) => throw inc
  case Value(v) =>
    BulletfBuild.playsound("sound/pickup01.wav")
    v
}
