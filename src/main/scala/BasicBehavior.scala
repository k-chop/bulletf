package com.github.whelmaze.bulletf


object BasicBehavior extends Behavior {

  def run(delta: Int)(unit: ScriptControlled) {
    import scala.math._
    import implicits.angle2double

    val vx = cos(unit.angle * Pi / 180.0f) * unit.speed * delta
    val vy = sin(unit.angle * Pi / 180.0f) * unit.speed * delta
    unit.pos.x += vx
    unit.pos.y += vy

    unit.angle.normalize()

  }
}
