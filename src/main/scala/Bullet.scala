package com.github.whelmaze.bulletf

import com.github.whelmaze.bulletf.script._
import java.io.{ FileInputStream, IOException }

import scala.collection.mutable.ArrayBuffer

class Bullet(val action: Behaivor, val resource: Symbol, var pos: Position, var speed: Double, var angle: Angle)
  extends HasCollision
{
  import Constants.script._

  val sprite: Sprite = new Sprite(resource)

  var time: Int = 0
  // 残りウェイトのカウンタ
  var waitCount: Int = -1
  // どの階層でウェイト中か
  var waitingNest: Int = 0
  // プログラムカウンタ
  val pc: Array[Int] = Array.fill(MAX_NEST){0}
  // ループカウンタ
  val lc: Array[Int] = Array.fill(MAX_NEST){-1}
  // 変数($0～$9)
  val vars: Array[Double] = Array.fill(MAX_NEST){0.0} // 変数は$0から$9まで
  // 当たり判定の半径
  val radius = sprite.texture.getImageWidth / 4.0

  def update(delta: Int) {
    action.run(delta)(this)
  }

  def draw() {
    sprite.draw(pos)
  }

  def inside = (0-(radius*2) <= pos.x  && pos.x <= constants.screenWidth+(radius*2) && 0-(radius*2) <= pos.y && pos.y <= constants.screenHeight+(radius*2))
  //override def draw() = Drawer.draw(texture, pos)
}