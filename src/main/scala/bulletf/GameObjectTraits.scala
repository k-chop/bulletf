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
  def update(delta: Int)
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

trait HasInnerFunc {

  protected class InnerUpdateFunc  {
    private[this] var delta: Int = 0
    def set(d: Int) { delta = d }
    val func: Runnable => Unit = b => b.update(delta)
  }

  protected lazy val updateFunc: InnerUpdateFunc = new InnerUpdateFunc
  protected val enableFunc = (b: BulletLike) => b.enable
  protected val drawFunc = (b: Drawable) => b.draw()
  protected val initFunc = (b: CanInit) => b.init()

}

trait OwnerLike {
  var ownObjects: mutable.ListBuffer[BulletLike]
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
  self: OwnerLike =>

  protected[this] lazy val nextAddPool = mutable.ListBuffer.empty[BulletLike]

  def fire(action: Behavior, kind: Symbol, pos: Position, angle: Angle, speed: Double) {
    nextAddPool += STGObjectFactory.newBullet(action, kind, pos, angle, speed)
  }
  def emit(action: Behavior, pos: Position, angle: Angle, speed: Double) {
    nextAddPool += STGObjectFactory.newEmitter(action, pos, angle, speed)
  }

}

