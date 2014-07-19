package bulletf

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
  def update()
}

trait CanInit {
  def init()
}

trait ParamCustomizable {
  def setParam(params: Map[Symbol, String]) {}
}

sealed trait DrawType
case object NormalDraw extends DrawType
case object AdditiveDraw extends DrawType
case class RotateDraw(speed: Double) extends DrawType

trait BulletLike extends ScriptControlled with Runnable with Drawable {
  var drawType: DrawType = NormalDraw
}

object CommonFunction {

  final val updateFunc = (b: Runnable) => b.update()
  final val enableFunc = (b: BulletLike) => b.enable
  final val drawFunc = (b: Drawable) => b.draw()
  final val initFunc = (b: CanInit) => b.init()

}

trait OwnerLike[T] {
  protected var ownObjects: mutable.ListBuffer[T]
  def childs: Seq[T] = ownObjects.toList
}

trait CanProduceToGlobal {
  def genEnemy(action: Behavior, kind: Symbol, pos: Position, angle: Angle, speed: Double) {
    //ownObjects += STGObjectFactory.newEnemy(action, kind, pos, angle, speed)
    Global.enemy_pool.set(STGObjectFactory.newEnemy(action, kind, pos, angle, speed))
  }
  def effect(action: Behavior, kind: Symbol, pos: Position, angle: Angle, speed: Double) {
    Global.effect_pool.set(STGObjectFactory.newEffect(action, kind, pos, angle, speed))
  }
}

trait CanProduceAll extends CanProduceToGlobal {
  self: OwnerLike[BulletLike] =>

  protected[this] lazy val nextAddPool = mutable.ListBuffer.empty[BulletLike]

  def fire(action: Behavior, kind: Symbol, pos: Position, angle: Angle, speed: Double) {
    nextAddPool += STGObjectFactory.newBullet(action, kind, pos, angle, speed)
  }
  def emit(action: Behavior, pos: Position, angle: Angle, speed: Double) {
    nextAddPool += STGObjectFactory.newEmitter(action, pos, angle, speed)
  }

}

