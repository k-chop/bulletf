package bulletf




object BasicBehavior extends Behavior {

  private[this] def isUpdated(unit: ScriptControlled): Boolean = unit match {
    case e: Enemy => e.status.posUpdated
    case _ => false
  }

  def run(unit: ScriptControlled) {
    import scala.math._
    import implicits.angle2double

    if (!isUpdated(unit)) {
      val vx = cos(unit.angle * Pi / 180.0f) * unit.speed * 16 // 16 = old_delta
      val vy = sin(unit.angle * Pi / 180.0f) * unit.speed * 16
      unit.pos.x += vx
      unit.pos.y += vy

      unit.angle.normalize()
    }

  }
}
