package com.github.whelmaze.bulletf

import collection.mutable

class Emitter {
  var bullets = mutable.ListBuffer.empty[Bullet]

  def update(delta: Int) {
    bullets.foreach(_.update(delta: Int))
  }

  def draw() {
    bullets.foreach(_.draw())
  }

  def length = bullets.size

  def clear() {
    // 消滅の際エフェクト出したりする場合もあるから、foreachでdisableにした方が良いかも？
    // 得点アイテムへの変化とかもすんだろ
    bullets.clear()
  }


}
