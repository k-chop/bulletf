package com.github.whelmaze.bulletf

import com.github.whelmaze.bulletf.script._

import scala.collection.mutable.ListBuffer

object STGObjectFactory {

  def init[T <: BulletLike](act: Behavior, t: T): T = {
    Global.needInit_pool.set(t)
    act match {
      case s: ScriptBehavior =>
        t.setParam(s.blocks.dataBlock)
      case _ =>
    }
    t
  }

  def initialEmitter(action: Behavior): Emitter = {
    init(action, new Emitter(action, Position(constants.screenWidth / 2.0, constants.screenHeight / 2.0 - 200), Angle.zero, 0))
  }

  def newEmitter(action: Behavior, pos: Position, angle: Angle, speed: Double): Emitter = {
    init(action, new Emitter(action, pos, angle, speed))
  }

  def newBullet(action: Behavior, kind: Symbol, pos: Position, angle: Angle, speed: Double): Bullet = {
    init(action, new Bullet(action, kind, pos, angle, speed))
  }

  def newEnemy(action: Behavior, kind: Symbol, pos: Position, angle: Angle, speed: Double): Enemy = {
    init(action, new Enemy(action, kind, pos, angle, speed))
  }

  def newShot(action: Behavior, kind: Symbol, pos: Position, angle: Angle, speed: Double): Shot = {
    init(action, new Shot(action, kind, pos, angle, speed))
  }

  def newEffect(action: Behavior, kind: Symbol, pos: Position, angle: Angle, speed: Double): Effect = {
    init(action, new Effect(action, kind, pos, angle, speed))
  }

}






