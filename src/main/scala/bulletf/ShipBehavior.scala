package bulletf



import org.lwjgl.input.Keyboard
import collection.mutable

object ShipBehavior {
  private val sqrt2 = 1.414213

  def move(ship: Ship) {
    import Input.keys._
    
    val plus = 4.0
    val ix = Input.x * plus
    val iy = Input.y * plus

    val slow = if (Input(LAZER)) 2 else 1
    
    // if ( key(KEY_RIGHT) ) vx += plus
    // if ( key(KEY_LEFT) ) vx -= plus
    // if ( key(KEY_DOWN) ) vy += plus
    // if ( key(KEY_UP) ) vy -= plus
    // if ( key(KEY_Z) ) {}

    ship.pos.x += (if (ix*iy != 0) ix/sqrt2/slow else ix/slow)
    ship.pos.y += (if (ix*iy != 0) iy/sqrt2/slow else iy/slow)
  }

  def shot(ship: Ship) {
    import Input.keys._

    if (ship.time % 3 == 0) {
      if (Input(SHOT) && !Input(LAZER)) {
        SoundSystem.playSymbol('shot01a, vol = 0.1f)
        ship.ownObjects +=
          STGObjectFactory.newShot(BasicBehavior, 'shot01, ship.pos.cloneBy(-6, -5), Angle(-90), 1.2)
        ship.ownObjects +=
          STGObjectFactory.newShot(BasicBehavior, 'shot01, ship.pos.cloneBy(+6, -5), Angle(-90), 1.2)
        ship.ownObjects +=
          STGObjectFactory.newShot(BasicBehavior, 'shot01, ship.pos.cloneBy(-10, -3), Angle(-100), 1.2)
        ship.ownObjects +=
          STGObjectFactory.newShot(BasicBehavior, 'shot01, ship.pos.cloneBy(+10, -3), Angle(-80), 1.2)
      } else if (Input(SHOT) && Input(LAZER)) {
        SoundSystem.playSymbol('shot01a, vol = 0.1f)
        ship.ownObjects +=
          STGObjectFactory.newShot(BasicBehavior, 'shot01, ship.pos.cloneBy(-15, 3), Angle(-90), 1.25)
        ship.ownObjects +=
          STGObjectFactory.newShot(BasicBehavior, 'shot01, ship.pos.cloneBy(-6, -3), Angle(-90), 1.25)
        ship.ownObjects +=
          STGObjectFactory.newShot(BasicBehavior, 'shot01, ship.pos.cloneBy(+6, -3), Angle(-90), 1.25)
        ship.ownObjects +=
          STGObjectFactory.newShot(BasicBehavior, 'shot01, ship.pos.cloneBy(+15, 3), Angle(-90), 1.25)
      }
    }
  }

}
