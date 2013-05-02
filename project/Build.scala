import sbt._
import Keys._

import java.io.File
import javax.sound.sampled._

import scala.concurrent.ops._

// さんこう
// http://www.ibm.com/developerworks/jp/java/library/j-5things12/#N101D9
// http://stackoverflow.com/questions/16057378/executing-a-task-dependency-for-test-failure-in-sbt

object BulletfBuild extends Build {

  def playsound(file: String) {
    spawn {
      val clip = AudioSystem.getClip()
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
  }

  lazy val root = Project(
    id = "bulletf",
    base = file(".")
  )

}
