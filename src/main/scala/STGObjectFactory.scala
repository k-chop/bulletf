package com.github.whelmaze.bulletf

import com.github.whelmaze.bulletf.script._

import scala.collection.mutable.ListBuffer

object STGObjectFactory {
  import implicits.angle2double

  final def newEmitter(action: Behavior, kind: Symbol): Emitter = {
    new Emitter(action, Position(constants.screenWidth / 2.0, constants.screenHeight / 2.0 - 200), 0, Angle(0) )
  }

  final def newBullet(action: Behavior, kind: Symbol, pos: Position, speed: Double, angle: Angle): Bullet = {
    new Bullet(action, kind, pos, speed, angle)
  }

  
}






