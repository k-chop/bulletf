package com.github.whelmaze.bulletf

import com.github.whelmaze.bulletf.Constants.script._
import collection.mutable

class Enemy(action: Behavior, resource: Symbol, var pos: Position, var angle: Angle, var speed: Double)
  extends BulletLike with OwnerLike with CanProduceAll with HasCollision with HasInnerFunc
{

  val sprite = Sprite.get(resource)
  val radius: Double = sprite.rect.w / 4.0

  var waitCount: Int = -1
  var waitingNest: Int = 0
  val pc = copyPc
  val lc = copyLc
  val vars = copyVars
  var enable: Boolean = true
  var live: Boolean = true
  var time: Int = 0

  // 体力
  var health: Int = 10

  var ownObjects = mutable.ListBuffer.empty[BulletLike]

  override def disable() {
    super.disable()
    live = false
  }

  override val updateFunc = new InnerUpdateFunc

  // スクリプトの実行が終わっていてまだ弾が残っているなら等速直線運動
  // 弾も消え、自身も画面外に行ったら死亡
  def onEndScript(delta: Int) {
    if (ownObjects.isEmpty && !live) disable()
    if (live) BasicBehavior.run(delta)(this)
  }

  def update(delta: Int) {
    if (enable) {
      if (!inside) live = false
      if (live) {
        action.run(delta)(this)
      }
      updateFunc.set(delta)
      ownObjects.foreach(updateFunc.func)
      if (time % 120 == 0) // per 2sec
        ownObjects = ownObjects.filter(enableFunc)
    }
  }

  def draw() {
    if (enable) {
      if (live) sprite.draw(pos)
      ownObjects.foreach(drawFunc)
    }
  }

  def damage(s: Shot) {
    health -= s.power
    if (health < 0) {
      disable()
    }
  }

  def size = ownObjects.size

  def inside = (0-(radius*2) <= pos.x  && pos.x <= constants.screenWidth+(radius*2) && 0-(radius*2) <= pos.y && pos.y <= constants.screenHeight+(radius*2))
}
