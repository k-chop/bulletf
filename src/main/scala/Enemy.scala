package com.github.whelmaze.bulletf

import com.github.whelmaze.bulletf.Constants.script._
import collection.mutable

class Enemy(action: Behavior, resource: Symbol, var pos: Position, var angle: Angle, var speed: Double)
  extends BulletLike with OwnerLike with CanProduceAll with HasCollision with HasInnerFunc
{

  val sprite = Sprite.get(resource)
  val radius: Double = sprite.rect.w / 4.0

  var live: Boolean = true
  // 体力
  var health: Int = 10

  var ownObjects = mutable.ListBuffer.empty[BulletLike]

  // initブロックの実行
  override def init() {
    action.init(this)
  }

  override def setParam(params: Map[Symbol, String]) {
    this.health = params.get('health) map { s: String =>
      s.toInt
    } getOrElse(10)
  }

  override def disable() {
    super.disable()
    live = false
  }

  // スクリプトの実行が終わっていてまだ弾が残っているなら等速直線運動
  // 弾も消え、自身も画面外に行ったら死亡
  def onEndScript(delta: Int) {
    if (ownObjects.isEmpty && !live) disable()
    if (live) BasicBehavior.run(delta)(this)
  }

  def update(delta: Int) {
    if (enable) {
      if (nextAddPool.nonEmpty) {
        ownObjects ++= nextAddPool // この時点でinitが済んでる
        nextAddPool.clear()
      }
      if (!inside) live = false
      if (live) {
        time += 1
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
      if (live) sprite.draw(pos, 0, 1.0, 1.0)
      ownObjects.foreach(drawFunc)
    }
  }

  def damage(s: Shot) {
    health -= s.power
    if (health < 0) {
      die()
    }
  }

  def die() {
    live = false
    // 死んだ時もスクリプトで制御し隊
    effect(BehaviorManager.get('death), 'invader01, pos.cloneBy(0,0), Angle.zero, 0)
  }

  def size = ownObjects.size

  def inside = (0-(radius*2) <= pos.x  && pos.x <= constants.screenWidth+(radius*2) && 0-(radius*2) <= pos.y && pos.y <= constants.screenHeight+(radius*2))
}
