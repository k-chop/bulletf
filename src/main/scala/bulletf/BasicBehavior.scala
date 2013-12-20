package bulletf




object BasicBehavior extends Behavior {

  private[this] def isUpdated(unit: ScriptControlled): Boolean = unit match {
    case e: Enemy => e.status.posUpdated
    case _ => false
  }

  def run(delta: Int)(unit: ScriptControlled) {
    import scala.math._
    import implicits.angle2double

    if (!isUpdated(unit)) {
      val vx = cos(unit.angle * Pi / 180.0f) * unit.speed * delta
      val vy = sin(unit.angle * Pi / 180.0f) * unit.speed * delta
      unit.pos.x += vx
      unit.pos.y += vy

      unit.angle.normalize()
    }

  }
}
