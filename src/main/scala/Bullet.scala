package com.github.whelmaze.bulletf

import com.github.whelmaze.bulletf.script._
import java.io.{ FileInputStream, IOException }

import scala.collection.mutable.ArrayBuffer

class Bullet(val action: Behavior, val resource: Symbol, var pos: Position, var angle: Angle, var speed: Double)
  extends BulletLike with HasCollision with CanProduceToGlobal
{
  import Constants.script._

  val sprite: Sprite = Sprite.get(resource)

/*  var waitCount = -1
  var waitingNest = 0

  val pc = copyPc
  val lc = copyLc
  val vars = copyVars
  var enable = true
  var time = 0*/

  // 当たり判定の半径
  val radius = 2.0

  override def init() {
    action.init(this)
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
    if (enable) sprite.draw(pos, /*(time % 360)*8*/angle.dir-90, 1.0, 1.0, time)
  }

  def inside = (0-(radius*2) <= pos.x  && pos.x <= constants.screenWidth+(radius*2) && 0-(radius*2) <= pos.y && pos.y <= constants.screenHeight+(radius*2))

}