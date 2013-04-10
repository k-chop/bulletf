package com.github.whelmaze.bulletf

import collection.mutable
import com.github.whelmaze.bulletf.Constants.script._

class Emitter(action: Behavior, var pos: Position, var angle: Angle, var speed: Double)
  extends BulletLike with OwnerLike with CanProduceAll
{

  var waitCount: Int = -1
  var waitingNest: Int = 0
  val pc: Array[Int] = Array.fill(MAX_NEST){0}
  val lc: Array[Int] = Array.fill(MAX_NEST){-1}
  val vars: Array[Double] = Array.fill(MAX_NEST){0.0}
  var enable = true
  var time = 0

  var ownObjects = mutable.ListBuffer.empty[BulletLike]

  // スクリプトの実行が終わっていたら、持ち弾が0になるまで待ってから死ぬ
  def onEndScript(delta: Int) {
    if (ownObjects.isEmpty) disable()
  }

  def update(delta: Int) {
    if (enable) {
      action.run(delta)(this)
      ownObjects.foreach(_.update(delta))
      if (time % 120 == 0) // per 2sec
        ownObjects = ownObjects.filter(_.enable)
    }
  }

  def draw() {
    if (enable) ownObjects.foreach(_.draw())
  }

  def size = ownObjects.size

  def clear() {
    // 消滅の際エフェクト出したりする場合もあるから、foreachでdisableにした方が良いかも？
    // 得点アイテムへの変化とかもすんだろ
    ownObjects.clear()
  }


}
