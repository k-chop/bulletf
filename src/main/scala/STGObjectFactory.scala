package com.github.whelmaze.bulletf

import com.github.whelmaze.bulletf.script._

import scala.collection.mutable.ListBuffer

object STGObjectFactory {
  import implicits.angle2double

  final def newEmitter(action: Behaivor, kind: Symbol): Bullet = kind match {
    case 'nullpo => {
      new Bullet(action, Resource.bullet.nullpo, Position(constants.screenWidth / 2.0, constants.screenHeight / 2.0 - 200), 0, 0 )
      //new Bullet3d(action, Resource.bullet.nullpo, Position(5, 5), 0, 0 )
    }
  }

  final def newBullet(action: Behaivor, kind: Symbol, pos: Position, speed: Double, angle: Angle): Bullet = kind match {
    case 'normal => 
      new Bullet3d(action, Resource.bullet.normal, pos, speed, angle )
    case 'mini =>
      new Bullet3d(action, Resource.bullet.mini, pos, speed, angle )
  }

  
}
