package bulletf



import org.lwjgl.input.Keyboard
import collection.mutable

object ShipBehavior {
  private final val sqrt2 = 1.414213

  def move(ship: Ship) {
    import Input.keys._
    
    val plus = 4.0
    val ix = Input.x * plus
    val iy = Input.y * plus

    val slow = if (Input(LASER)) 2 else 1

    ship.pos.x += (if (ix*iy != 0) ix/sqrt2/slow else ix/slow)
    ship.pos.y += (if (ix*iy != 0) iy/sqrt2/slow else iy/slow)
  }

  def shot(ship: Ship) {
    import Input.keys._

    @inline val shotF = (op: ShipOption) =>
        ship.ownObjects += STGObjectFactory.newShot(BasicBehavior, 'shot01, op.pos.cloneBy(0, -5), Angle(-90), 1.2)

    @inline val laserF = (op: ShipOption) =>
        ship.ownObjects += STGObjectFactory.newShot(BasicBehavior, 'shot02, op.pos.cloneBy(0, -5), Angle(-90), 1.3)

    if (ship.time % 3 == 0) {
      if (Input(SHOT) && !Input(LASER)) {
        SoundSystem.playSymbol('shot01a, vol = 0.1f)
        ship.options foreach shotF
      } else if (Input(SHOT) && Input(LASER)) {
        SoundSystem.playSymbol('shot01a, vol = 0.1f)
        ship.options foreach laserF
      }
    }
  }

}
