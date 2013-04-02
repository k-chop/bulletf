package com.github.whelmaze.bulletf

import org.newdawn.slick.Sound

object SoundEffect {
  val arr = Array(
    new Sound("sound/c_cursor06.wav"),
    new Sound("sound/bashi.wav")
  )

  def play(id: Int) {
    require(id < arr.length)
    arr(id).play(1.0f, 0.5f)
  }

  def playSymbol(s: Symbol) = s match {
    case 'test =>
      play(0)
    case 'test2 =>
      play(1)
    case _ =>
  }

  def free() {
    arr.foreach(_.release())
  }
}
