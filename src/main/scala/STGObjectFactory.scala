package com.github.whelmaze.bulletf

import com.github.whelmaze.bulletf.script._

import scala.collection.mutable.ListBuffer

object STGObjectFactory {

  def initialEmitter(action: Behavior): Emitter = {
    new Emitter(action, Position(constants.screenWidth / 2.0, constants.screenHeight / 2.0 - 200), Angle.zero, 0)
  }

  def newEmitter(action: Behavior, pos: Position, angle: Angle, speed: Double): Emitter = {
    new Emitter(action, pos, angle, speed)
  }

  def newBullet(action: Behavior, kind: Symbol, pos: Position, angle: Angle, speed: Double): Bullet = {
    new Bullet(action, kind, pos, angle, speed)
  }

  def newEnemy(action: Behavior, kind: Symbol, pos: Position, angle: Angle, speed: Double): Enemy = {
    new Enemy(action, kind, pos, angle, speed)
  }

  def newShot(action: Behavior, kind: Symbol, pos: Position, angle: Angle, speed: Double): Shot = {
    new Shot(action, kind, pos, angle, speed)
  }

  def newEffect(action: Behavior, kind: Symbol, pos: Position, angle: Angle, speed: Double): Effect = {
    new Effect(action, kind, pos, angle, speed)
  }

}






