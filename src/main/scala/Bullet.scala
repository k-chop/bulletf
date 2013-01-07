package com.github.whelmaze.bulletf

import com.github.whelmaze.bulletf.script._
import java.io.{ FileInputStream, IOException }

import scala.collection.mutable.ArrayBuffer

class Bullet(val action: Behaivor, _resource: Symbol, pos: Position, _speed: Double, _angle: Double) extends Sprite(_resource, pos, _speed, _angle) with HasCollision {
  import Constants.script._
  
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
  radius = texture.getImageWidth / 4.0

  override def update(delta: Int) = action.run(delta)(this)
  
  //override def draw() = Drawer.draw(texture, pos)
  
}
