package com.github.whelmaze.bulletf



object BasicBehaivor extends Behaivor {

  final def run(delta: Int)(implicit bullet: Bullet) = {
    import scala.math._
    import implicits.angle2double

    val vx = cos(bullet.angle * Pi / 180.0f) * bullet.speed * delta
    val vy = sin(bullet.angle * Pi / 180.0f) * bullet.speed * delta
    bullet.pos = bullet.pos.add(vx, vy)

    bullet.angle = bullet.angle.normalize

  }
}
