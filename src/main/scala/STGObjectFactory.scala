package com.github.whelmaze.bulletf

import com.github.whelmaze.bulletf.script._

import scala.collection.mutable.ListBuffer

object STGObjectFactory {

  def initialEmitter(action: Behavior): Emitter = {
    val e = new Emitter(action, Position(constants.screenWidth / 2.0, constants.screenHeight / 2.0 - 200), Angle.zero, 0)
    Global.needInit_pool.set(e)
    e
  }

  def newEmitter(action: Behavior, pos: Position, angle: Angle, speed: Double): Emitter = {
    val e = new Emitter(action, pos, angle, speed)
    Global.needInit_pool.set(e)
    e
  }

  def newBullet(action: Behavior, kind: Symbol, pos: Position, angle: Angle, speed: Double): Bullet = {
    val e = new Bullet(action, kind, pos, angle, speed)
    Global.needInit_pool.set(e)
    e
  }

  def newEnemy(action: Behavior, kind: Symbol, pos: Position, angle: Angle, speed: Double): Enemy = {
    val e = new Enemy(action, kind, pos, angle, speed)
    Global.needInit_pool.set(e)
    e
  }

  def newShot(action: Behavior, kind: Symbol, pos: Position, angle: Angle, speed: Double): Shot = {
    val e = new Shot(action, kind, pos, angle, speed)
    Global.needInit_pool.set(e)
    e
  }

  def newEffect(action: Behavior, kind: Symbol, pos: Position, angle: Angle, speed: Double): Effect = {
    val e = new Effect(action, kind, pos, angle, speed)
    Global.needInit_pool.set(e)
    e
  }

}






