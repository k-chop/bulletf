package com.github.whelmaze.bulletf

import org.newdawn.slick.Sound

object SoundEffect {
  // この辺りはいずれ設定ファイルから読み込むので適当で
  val arr = Array(
    new Sound("sound/c_cursor06.wav"),
    new Sound("sound/bashi.wav")
  )
  val cooltimeDef = Array(
    3, 4
  )
  val cooltime = Array.fill(arr.length)(0)

  def update(delta: Int) {
    var i = arr.length - 1
    while(0 <= i) {
      if (0 < cooltime(i)) cooltime(i) -= 1
      i -= 1
    }
  }

  def play(id: Int) {
    require(id < arr.length)
    if (cooltime(id) < 1) {
      arr(id).play(1.0f, 0.5f)
      cooltime(id) = cooltimeDef(id)
    }
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
