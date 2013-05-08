package com.github.whelmaze.bulletf


object Rect {

  val min = Rect(0,0,1,1)

}

case class Rect(x: Int, y:Int, w: Int, h: Int) {

  def cloneBy(ax: Int, ay: Int, aw: Int, ah: Int) = copy(x+ax, y+ay, aw, ah)

}
