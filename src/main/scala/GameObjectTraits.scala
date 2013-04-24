package com.github.whelmaze.bulletf

import collection.mutable

trait Movable {
  var pos: Position
  var speed: Double
  var angle: Angle
}

trait Drawable {
  def draw()
}

trait Runnable {
  def update(delta: Int)
}

trait BulletLike extends ScriptControlled with Runnable with Drawable

trait HasInnerFunc {

  protected class InnerUpdateFunc  {
    private[this] var delta: Int = 0
    def set(d: Int) { delta = d }
    val func: BulletLike => Unit = b => b.update(delta)
  }

  protected val updateFunc: InnerUpdateFunc
  protected val enableFunc = (b: BulletLike) => b.enable
  protected val drawFunc = (b: BulletLike) => b.draw()

}

trait OwnerLike {
  var ownObjects: mutable.ListBuffer[BulletLike]
}

trait CanProduceAll {
  self: OwnerLike =>

  def fire(action: Behavior, kind: Symbol, pos: Position, angle: Angle, speed: Double) {
    ownObjects += STGObjectFactory.newBullet(action, kind, pos, angle, speed)
  }
  def emit(action: Behavior, pos: Position, angle: Angle, speed: Double) {
    ownObjects += STGObjectFactory.newEmitter(action, pos, angle, speed)
  }
  def genEnemy(action: Behavior, kind: Symbol, pos: Position, angle: Angle, speed: Double) {
    //ownObjects += STGObjectFactory.newEnemy(action, kind, pos, angle, speed)
    Global.enemy_pool.set(STGObjectFactory.newEnemy(action, kind, pos, angle, speed))
  }

}

