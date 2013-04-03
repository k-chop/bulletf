package com.github.whelmaze.bulletf

import com.github.whelmaze.bulletf.script._
import java.io.{ FileInputStream, IOException }

import scala.collection.mutable.ArrayBuffer

class Bullet(val action: Behavior, val resource: Symbol, var pos: Position, var angle: Angle, var speed: Double)
  extends BulletLike with HasCollision
{
  import Constants.script._

  val sprite: Sprite = new Sprite(resource)

  var waitCount = -1
  var waitingNest = 0
  val pc = Array.fill(MAX_NEST){0}
  val lc = Array.fill(MAX_NEST){-1}
  val vars = Array.fill(MAX_NEST){0.0}
  var enable = true
  var time = 0

  // 当たり判定の半径
  val radius = sprite.texture.getImageWidth / 4.0

  // スクリプトの実行が終わったら等速直線運動へシフト
  def onEndScript(delta: Int) {
    BasicBehavior.run(delta)(this)
  }

  def update(delta: Int) {
    if (enable) action.run(delta)(this)
    if (!inside) disable()
  }

  def draw() {
    if (enable) sprite.draw(pos)
  }

  def inside = (0-(radius*2) <= pos.x  && pos.x <= constants.screenWidth+(radius*2) && 0-(radius*2) <= pos.y && pos.y <= constants.screenHeight+(radius*2))

}