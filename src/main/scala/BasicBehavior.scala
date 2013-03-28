package com.github.whelmaze.bulletf


object BasicBehavior extends Behavior {

  final def run(delta: Int)(implicit unit: ScriptedMove) {
    import scala.math._
    import implicits.angle2double

    unit.time += 1

    val vx = cos(unit.angle * Pi / 180.0f) * unit.speed * delta
    val vy = sin(unit.angle * Pi / 180.0f) * unit.speed * delta
    unit.pos = unit.pos.add(vx, vy)

    unit.angle = unit.angle.normalized

  }
}
