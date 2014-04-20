package bulletf



import script._
import java.io.{ FileInputStream, IOException }

import scala.collection.mutable.ArrayBuffer

object Bullet {

  final private[Bullet] val psdt: String PartialFunction DrawType = {
    case s if s.split(":").head == "rotate" =>
      RotateDraw(s.split(":")(1).toDouble)
    case s if s == "additive" =>
      AdditiveDraw
    case _ =>
      NormalDraw
  }

}

class Bullet(val action: Behavior, val resource: Symbol, var pos: Position, var angle: Angle, var speed: Double)
  extends BulletLike with HasCollision with CanProduceToGlobal
{

  val sprite: Sprite = Sprite.get(resource)

  // 当たり判定の半径
  val radius = 2.0

  override def init() {
    action.init(this)
  }

  override def setParam(params: Map[Symbol, String]) {
    val t = params.get('draw_type).map(Bullet.psdt)
    drawType = if (t.isEmpty) NormalDraw else t.get
  }

  override def disable() {
    enable = false
  }

  // スクリプトの実行が終わったら等速直線運動へシフト
  def onEndScript() {
    BasicBehavior.run(this)
  }

  def update() {
    if (enable) {
      time += 1
      action.run(this)
    }
    if (!inside) disable()
  }

  def draw() {
    if (enable) {
      drawType match {
        case RotateDraw(rspd) =>
          sprite.draw(pos, (time % 360) * rspd, 1.0, 1.0, time)
        case _ =>
          sprite.draw(pos, angle.dir-90, 1.0, 1.0, time)
      }
    }
  }

  def inside = 0 - (radius * 2) <= pos.x && pos.x <= constants.screenWidth + (radius * 2) && 0 - (radius * 2) <= pos.y && pos.y <= constants.screenHeight + (radius * 2)

}