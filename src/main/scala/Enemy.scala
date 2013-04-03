package com.github.whelmaze.bulletf

import com.github.whelmaze.bulletf.Constants.script._
import collection.mutable

class Enemy(action: Behavior, resource: Symbol, var pos: Position, var angle: Angle, var speed: Double)
  extends BulletLike with OwnerLike with CanProduceAll with HasCollision
{

  val sprite = new Sprite(resource)
  val radius: Double = sprite.texture.getImageWidth / 4.0

  var waitCount: Int = -1
  var waitingNest: Int = 0
  val pc: Array[Int] = Array.fill(MAX_NEST){0}
  val lc: Array[Int] = Array.fill(MAX_NEST){-1}
  val vars: Array[Double] = Array.fill(MAX_NEST){0.0}
  var enable: Boolean = true
  var time: Int = 0

  var ownObjects = mutable.ListBuffer.empty[BulletLike]

  // スクリプトの実行が終わった時に呼び出される。
  def onEndScript(delta: Int) {
    if (ownObjects.isEmpty) disable()
    BasicBehavior.run(delta)(this)
  }

  def update(delta: Int) {
    if (enable) {
      action.run(delta)(this)
      ownObjects.foreach(_.update(delta))
      ownObjects = ownObjects.filter(_.enable)
    }
  }

  def draw() {
    sprite.draw(pos)
    if (enable) ownObjects.foreach(_.draw())
  }

  def size = ownObjects.size
}
