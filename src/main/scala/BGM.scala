package com.github.whelmaze.bulletf

import org.newdawn.slick.Music

object BGM {

  val arr = Array(
    new Music("music/boss1[b].ogg")
  )

  def play(id: Int) {
    arr(id).loop(1f, 0.5f)
  }

  def free() {
    arr.foreach(_.release())
  }

}
