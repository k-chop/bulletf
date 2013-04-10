package com.github.whelmaze.bulletf

import com.github.whelmaze.bulletf.Constants.script._
import collection.mutable

class Enemy(action: Behavior, resource: Symbol, var pos: Position, var angle: Angle, var speed: Double)
  extends BulletLike with OwnerLike with CanProduceAll with HasCollision with HasInnerFunc
{

  val sprite = Sprite.get(resource)
  val radius: Double = sprite.rect.x / 4.0

  var waitCount: Int = -1
  var waitingNest: Int = 0
  val pc: Array[Int] = Array.fill(MAX_NEST){0}
  val lc: Array[Int] = Array.fill(MAX_NEST){-1}
  val vars: Array[Double] = Array.fill(MAX_NEST){0.0}
  var enable: Boolean = true
  var time: Int = 0

  var ownObjects = mutable.ListBuffer.empty[BulletLike]

  override val updateFunc = new InnerUpdateFunc

  // スクリプトの実行が終わっていてまだ弾が残っているなら等速直線運動
  // 弾も消え、自身も画面外に行ったら死亡
  def onEndScript(delta: Int) {
    if (ownObjects.isEmpty && inside) disable()
    BasicBehavior.run(delta)(this)
  }

  def update(delta: Int) {
    if (enable) {
      action.run(delta)(this)
      updateFunc.set(delta)
      ownObjects.foreach(updateFunc.func)
      if (time % 120 == 0) // per 2sec
        ownObjects = ownObjects.filter(enableFunc)
    }
  }

  def draw() {
    sprite.draw(pos)
    if (enable) ownObjects.foreach(drawFunc)
  }

  def size = ownObjects.size

  def inside = (0-(radius*2) <= pos.x  && pos.x <= constants.screenWidth+(radius*2) && 0-(radius*2) <= pos.y && pos.y <= constants.screenHeight+(radius*2))
}
