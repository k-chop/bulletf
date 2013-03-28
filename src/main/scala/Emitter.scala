package com.github.whelmaze.bulletf

import collection.mutable
import com.github.whelmaze.bulletf.Constants.script._

class Emitter(action: Behavior, var pos: Position, var speed: Double, var angle: Angle) extends ScriptedMove {

  var waitCount: Int = -1
  var waitingNest: Int = 0
  val pc: Array[Int] = Array.fill(MAX_NEST){0}
  val lc: Array[Int] = Array.fill(MAX_NEST){-1}
  val vars: Array[Double] = Array.fill(MAX_NEST){0.0}
  var enable = true
  var time = 0

  var bullets = mutable.ListBuffer.empty[Bullet]

  def produce(action: Behavior, kind: Symbol, pos: Position, speed: Double, angle: Angle) {
    bullets += STGObjectFactory.newBullet(action, kind, pos, speed, angle)
  }

  // スクリプトの実行が終わっていたら、持ち弾が0になるまで待ってから死ぬ
  def onEndScript(delta: Int) {
    if (bullets.isEmpty) disable()
  }

  def update(delta: Int) {
    if (enable) {
      action.run(delta)(this)
      bullets.foreach(_.update(delta))
      bullets = bullets.filter(_.enable)
    }
  }

  def draw() {
    if (enable) bullets.foreach(_.draw())
  }

  def size = bullets.size

  def clear() {
    // 消滅の際エフェクト出したりする場合もあるから、foreachでdisableにした方が良いかも？
    // 得点アイテムへの変化とかもすんだろ
    bullets.clear()
  }


}
