import sbt._
import Keys._

import java.io.File
import javax.sound.sampled._

import scala.concurrent._
import scala.concurrent.duration._

import ExecutionContext.Implicits.global

// さんこう
// http://www.ibm.com/developerworks/jp/java/library/j-5things12/#N101D9
// http://stackoverflow.com/questions/16057378/executing-a-task-dependency-for-test-failure-in-sbt

object Sound {

  def play(file: String) {
    val f = future {
      val clip = AudioSystem.getClip
      clip.addLineListener(new LineListener(){
        def update(e: LineEvent) {
          if (e.getType == LineEvent.Type.STOP) {
            clip.synchronized { clip.notify() }
          }
        }
      })
      val soundfile = new File(file)
      val stream = AudioSystem.getAudioInputStream(soundfile)
      clip.open(stream)
      clip.start()
      clip.synchronized {
        clip.wait()
      }
      clip.drain()
      clip.close()
      stream.close()
    }
    Await.result(f, 1 seconds)
  }
}

object BulletfBuild extends Build {

  lazy val root = Project(
    id = "bulletf",
    base = file(".")
  )

}
