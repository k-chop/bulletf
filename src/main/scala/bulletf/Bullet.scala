package bulletf



import script._
import java.io.{ FileInputStream, IOException }

import scala.collection.mutable.ArrayBuffer

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
    def rparamEx(s: String) = s.split(":")

    drawType = params.get('draw_type).map {
      case s if rparamEx(s).head == "rotate" =>
        RotateDraw(rparamEx(s)(1).toDouble)
      case s if s == "additive" =>
        AdditiveDraw
      case _ =>
        NormalDraw
    }.getOrElse(NormalDraw)

  }

  override def disable() {
    enable = false
  }

  // スクリプトの実行が終わったら等速直線運動へシフト
  def onEndScript(delta: Int) {
    BasicBehavior.run(delta)(this)
  }

  def update(delta: Int) {
    if (enable) {
      time += 1
      action.run(delta)(this)
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